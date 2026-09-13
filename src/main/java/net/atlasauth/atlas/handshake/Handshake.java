package net.atlasauth.atlas.handshake;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Cross-property SSO handshake — the satellite property's server-side redeem step.
 *
 * <p>When a satellite (a property on a DIFFERENT registrable domain than the main
 * app) has no local session, it bounces the browser through
 * {@code GET {fapiOrigin}/v1/client/handshake} which — if the user has an Atlas
 * session — redirects back with
 * {@code ?__atlas_hs=ok&__atlas_hu=<userId>&__atlas_hn=<nonce>}. This helper
 * exchanges that single-use nonce for a fresh session, server-to-server, so the
 * tokens are returned in the response BODY (never a URL). The satellite's
 * servlet/filter then sets them as its OWN first-party cookies.
 *
 * <p>The bounce is PKCE-bound (RFC 7636, {@code S256}): because the nonce rides a
 * loggable return URL, it is not a bearer credential on its own. On the outbound
 * bounce the satellite generates a {@link Pkce} pair, sends only the
 * {@code code_challenge} in the URL, and stashes the verifier server-side (an
 * HttpOnly {@code __atlas_hv} cookie, path {@code /}, ~300s); the return trip
 * reads it back and presents it to redeem, so a nonce scraped from a log cannot
 * be redeemed without the verifier that never left this origin.
 *
 * <pre>{@code
 * // 1) No local session yet — bounce the browser, stashing the verifier.
 * if (Handshake.readHandshakeParams(request.getRequestURL() + "?" + request.getQueryString()) == null
 *         && request.getParameter("__atlas_hs") == null) {
 *     HandshakeRedirect redirect = Handshake.handshakeRedirect(
 *             "https://id.atlasauth.net", "pk_live_...", currentUrl);
 *     Cookie hv = new Cookie("__atlas_hv", redirect.codeVerifier());
 *     hv.setPath("/");
 *     hv.setSecure(true);
 *     hv.setHttpOnly(true);
 *     hv.setMaxAge(300);
 *     response.addCookie(hv);
 *     response.sendRedirect(redirect.url());
 *     return;
 * }
 *
 * // 2) Coming back from the handshake — redeem with the stashed verifier.
 * HandshakeParams params = Handshake.readHandshakeParams(request.getRequestURL()
 *         + "?" + request.getQueryString());
 * if (params != null) {
 *     String verifier = readCookie(request, "__atlas_hv"); // your cookie lookup
 *     HandshakeSession session = Handshake.redeemHandshake(
 *             "https://id.atlasauth.net", "pk_live_...", params.userId(), params.nonce(), verifier);
 *     if (session != null) {
 *         Cookie sessionCookie = new Cookie("__session", session.jwt());
 *         sessionCookie.setPath("/");
 *         sessionCookie.setSecure(true);
 *         response.addCookie(sessionCookie);
 *
 *         Cookie refreshCookie = new Cookie("__atlas_rt", session.refreshToken());
 *         refreshCookie.setPath("/");
 *         refreshCookie.setSecure(true);
 *         refreshCookie.setHttpOnly(true);
 *         response.addCookie(refreshCookie);
 *     }
 *     // else: nonce was missing/expired/used — treat as signed-out and send to sign-in.
 * }
 * }</pre>
 *
 * <p>(Same-registrable-domain SUBDOMAINS don't need this — the handshake sets a
 * parent-domain cookie directly; this is only the cross-domain path.)
 */
public final class Handshake {

    /** Default per-request timeout for {@link #redeemHandshake}. */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Shared CSPRNG for PKCE verifiers. {@link SecureRandom} is thread-safe. */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /** RFC 7636 requires the base64url encoding <em>without</em> padding. */
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private Handshake() {
    }

    /**
     * Generate a PKCE (RFC 7636, {@code S256}) pair for one handshake bounce.
     *
     * <p>Why: the handshake nonce comes back on a return URL, and URLs get logged
     * (proxies, access logs, {@code Referer}, history). So the nonce must not be a
     * bearer credential on its own. This binds the bounce to the origin that
     * started it — put the {@link Pkce#challenge()} in the handshake URL, stash the
     * {@link Pkce#verifier()} server-side (an HttpOnly {@code __atlas_hv} cookie,
     * path {@code /}, ~300s), and present the verifier at redeem. A nonce scraped
     * from a log then cannot be redeemed without the verifier, which never left
     * this origin and never appeared in a URL.
     *
     * <p>The verifier is the base64url (no padding) of 32 fresh
     * {@link SecureRandom} bytes; the challenge is the base64url (no padding) of
     * {@code SHA-256} over the verifier's US-ASCII bytes.
     *
     * @return a fresh, single-use {@link Pkce} pair
     */
    public static Pkce generatePkce() {
        byte[] raw = new byte[32];
        SECURE_RANDOM.nextBytes(raw);
        String verifier = URL_ENCODER.encodeToString(raw);
        String challenge = URL_ENCODER.encodeToString(sha256(verifier.getBytes(StandardCharsets.US_ASCII)));
        return new Pkce(verifier, challenge);
    }

    private static byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated on every conforming JRE (JCA "MessageDigest"
            // standard names), so this branch is unreachable; if it ever fires the
            // environment is broken beyond a recoverable auth failure — fail loud.
            throw new IllegalStateException("SHA-256 MessageDigest unavailable", e);
        }
    }

    /**
     * Redeem a handshake nonce for a fresh session, server-to-server.
     *
     * <p>POSTs to {@code {fapiOrigin}/v1/client/handshake/redeem} with header
     * {@code X-Publishable-Key: {publishableKey}} and JSON body
     * {@code {"user_id": userId, "nonce": nonce, "code_verifier": codeVerifier}}.
     *
     * <p>The {@code code_verifier} is the PKCE (RFC 7636) secret stashed on the
     * outbound bounce (see {@link #handshakeRedirect} / {@link #generatePkce()},
     * documented as the {@code __atlas_hv} cookie). The server now REQUIRES it:
     * without the verifier that matches the {@code code_challenge} sent up front, a
     * nonce lifted from a logged return URL cannot be redeemed.
     *
     * <p>Returns {@code null} when the nonce is missing/expired/already used, when
     * the verifier does not match, or on any transport/parse failure. A bad nonce
     * or verifier is a normal signed-out signal — the caller should fall back to
     * sign-in — NOT an error that takes the page down, so this never throws on an
     * auth failure.
     *
     * @param fapiOrigin     origin of the Atlas Frontend API, e.g. {@code https://id.atlasauth.net}
     * @param publishableKey the instance publishable key ({@code pk_...})
     * @param userId         the {@code __atlas_hu} value from the return URL
     * @param nonce          the single-use {@code __atlas_hn} nonce from the return URL
     * @param codeVerifier   the PKCE verifier stashed on the bounce (the {@code __atlas_hv} value)
     * @return a fresh {@link HandshakeSession}, or {@code null} if the nonce could not be redeemed
     */
    public static HandshakeSession redeemHandshake(String fapiOrigin,
                                                   String publishableKey,
                                                   String userId,
                                                   String nonce,
                                                   String codeVerifier) {
        return redeemHandshake(fapiOrigin, publishableKey, userId, nonce, codeVerifier,
                HttpClient.newHttpClient());
    }

    /**
     * As {@link #redeemHandshake(String, String, String, String, String)}, but
     * reusing a shared/tuned {@link HttpClient} (proxy, connection pool, timeouts).
     */
    public static HandshakeSession redeemHandshake(String fapiOrigin,
                                                   String publishableKey,
                                                   String userId,
                                                   String nonce,
                                                   String codeVerifier,
                                                   HttpClient http) {
        if (fapiOrigin == null || publishableKey == null || userId == null || nonce == null
                || codeVerifier == null) {
            return null;
        }

        URI uri = URI.create(fapiOrigin.replaceAll("/+$", "") + "/v1/client/handshake/redeem");

        String body;
        try {
            body = MAPPER.writeValueAsString(
                    Map.of("user_id", userId, "nonce", nonce, "code_verifier", codeVerifier));
        } catch (Exception e) {
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(DEFAULT_TIMEOUT)
                .header("content-type", "application/json")
                .header("accept", "application/json")
                .header("X-Publishable-Key", publishableKey)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            // Transport failure — a signed-out signal, never an exception.
            return null;
        }

        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            return null;
        }

        try {
            JsonNode data = MAPPER.readTree(response.body());
            String jwt = text(data, "jwt");
            String refreshToken = text(data, "refresh_token");
            if (jwt == null || refreshToken == null) {
                return null;
            }
            String sessionId = text(data, "session_id");
            JsonNode expires = data.get("expires_in");
            return new HandshakeSession(
                    jwt,
                    refreshToken,
                    sessionId != null ? sessionId : "",
                    expires != null && expires.isNumber() ? expires.asLong() : 0L);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract the handshake parameters from a return URL (or its query string).
     *
     * <p>Returns a {@link HandshakeParams} when {@code __atlas_hs} equals
     * {@code "ok"} and both {@code __atlas_hu} (userId) and {@code __atlas_hn}
     * (nonce) are present; otherwise {@code null}.
     *
     * @param urlOrQuery a full URL, or a bare query string (with or without a
     *                   leading {@code ?})
     * @return the parsed params, or {@code null} if this is not a successful handshake return
     */
    public static HandshakeParams readHandshakeParams(String urlOrQuery) {
        if (urlOrQuery == null) {
            return null;
        }
        int q = urlOrQuery.indexOf('?');
        String query = q >= 0 ? urlOrQuery.substring(q + 1) : urlOrQuery;
        // Drop a fragment if the query string was pulled from a full URL.
        int hash = query.indexOf('#');
        if (hash >= 0) {
            query = query.substring(0, hash);
        }
        if (query.isEmpty()) {
            return null;
        }

        Map<String, String> params = new HashMap<>();
        for (String pair : query.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            int eq = pair.indexOf('=');
            String key = eq >= 0 ? pair.substring(0, eq) : pair;
            String value = eq >= 0 ? pair.substring(eq + 1) : "";
            params.putIfAbsent(decode(key), decode(value));
        }

        if (!"ok".equals(params.get("__atlas_hs"))) {
            return null;
        }
        String userId = params.get("__atlas_hu");
        String nonce = params.get("__atlas_hn");
        return userId != null && !userId.isEmpty() && nonce != null && !nonce.isEmpty()
                ? new HandshakeParams(userId, nonce)
                : null;
    }

    /**
     * Build the one-time PKCE-bound redirect a satellite sends the browser to when
     * it has no local session:
     * {@code {fapiOrigin}/v1/client/handshake?publishable_key={pk}&redirect_url={currentUrl}&code_challenge={challenge}}.
     *
     * <p>This generates a {@link Pkce} pair and puts only the {@code code_challenge}
     * in the URL. The returned {@link HandshakeRedirect} also carries the
     * {@link HandshakeRedirect#codeVerifier()} — the caller MUST stash it
     * server-side (documented as an HttpOnly {@code __atlas_hv} cookie, path
     * {@code /}, ~300s) and pass it back to {@link #redeemHandshake} on the return
     * trip. The verifier is a secret and must never appear in a URL; only the
     * one-way challenge does.
     *
     * <p>Guard the redirect by the <em>absence</em> of an {@code __atlas_hs} query
     * param on the current request so it never loops — if the browser is already
     * coming back from the handshake, redeem (or treat as signed-out) instead of
     * bouncing again.
     *
     * @param fapiOrigin     origin of the Atlas Frontend API
     * @param publishableKey the instance publishable key ({@code pk_...})
     * @param currentUrl     the satellite URL to return to after the handshake
     * @return the redirect {@link HandshakeRedirect#url()} plus the verifier to stash
     */
    public static HandshakeRedirect handshakeRedirect(String fapiOrigin, String publishableKey, String currentUrl) {
        Pkce pkce = generatePkce();
        String url = fapiOrigin.replaceAll("/+$", "") + "/v1/client/handshake"
                + "?publishable_key=" + URLEncoder.encode(publishableKey, StandardCharsets.UTF_8)
                + "&redirect_url=" + URLEncoder.encode(currentUrl, StandardCharsets.UTF_8)
                + "&code_challenge=" + URLEncoder.encode(pkce.challenge(), StandardCharsets.UTF_8);
        return new HandshakeRedirect(url, pkce.verifier());
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && value.isTextual() && !value.asText().isEmpty() ? value.asText() : null;
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
