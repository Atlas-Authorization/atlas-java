package net.atlasauth.atlas.rp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.atlasauth.atlas.AtlasApiException;
import net.atlasauth.atlas.ErrorItem;
import net.atlasauth.atlas.verify.JwksCache;
import net.atlasauth.atlas.verify.SigningAlgs;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The server-side OpenID Connect relying party for the hosted redirect: state, nonce and PKCE (S256) carried
 * in ONE sealed short-lived cookie, the code exchange, ID-token verification (algorithm pinned, {@code iss},
 * {@code aud}/{@code azp}, {@code exp}/{@code iat}, {@code nonce}, {@code at_hash}), keep-alive and server-side
 * sign-out. The core is servlet-agnostic (plain maps and strings in, redirect + cookies out); the
 * {@link RelyingPartyFilter} wires it into Jakarta Servlet and Spring.
 *
 * <pre>{@code
 * RelyingParty rp = new RelyingParty(RpConfig.builder(issuer, clientId, "https://app.example/auth/callback", secret)
 *     .clientSecret(clientSecret).secretKey(atlasSecretKey).build());
 * RelyingParty.Redirect go = rp.start(Map.of("return_to", "/dashboard"));     // 302 to go.location(), set go.cookies()
 * RelyingParty.CallbackResult done = rp.callback(queryMap, cookieHeader);      // done.user().sub(), done.user().sid() ...
 * }</pre>
 *
 * <p>The sealed flow cookie ({@code s1.<iv>.<ciphertext>}, AES-256-GCM, HKDF-SHA256 key) is the same format
 * the TypeScript relying party writes.
 */
public final class RelyingParty {

    /** Who signed in: the ID-token claims an app needs. */
    public record User(String sub, String sid, String email, boolean emailVerified, String name, String externalId,
                       String orgId, String orgRole, Long authTime) {
    }

    public record Tokens(String idToken, String accessToken, String refreshToken, long expiresAt) {
    }

    /** A 302 to {@code location} with these {@code Set-Cookie} values. */
    public record Redirect(String location, List<String> cookies) {
    }

    /**
     * A verified sign-in. {@code claims} is every verified ID-token claim; {@code returnTo} is the safe path the sign-in
     * carried or null; {@code clearCookie} drops the spent flow cookie (send it as {@code Set-Cookie}).
     */
    public record CallbackResult(User user, Tokens tokens, Map<String, Object> claims, String returnTo, String clearCookie) {
    }

    /** Per-request overrides for {@link #start(Map, StartOptions)}. */
    public static final class StartOptions {
        String prompt;
        String loginHint;
        String uiLocales;
        Integer maxAge;
        String organization;
        String returnTo;
        String expectedSub;
        String scope;
        final Map<String, String> extraParams = new LinkedHashMap<>();

        public StartOptions prompt(String v) {
            prompt = v;
            return this;
        }

        public StartOptions loginHint(String v) {
            loginHint = v;
            return this;
        }

        public StartOptions uiLocales(String v) {
            uiLocales = v;
            return this;
        }

        public StartOptions maxAge(int seconds) {
            maxAge = seconds;
            return this;
        }

        public StartOptions organization(String v) {
            organization = v;
            return this;
        }

        public StartOptions returnTo(String v) {
            returnTo = v;
            return this;
        }

        public StartOptions scope(String v) {
            scope = v;
            return this;
        }

        public StartOptions extraParam(String name, String value) {
            extraParams.put(name, value);
            return this;
        }
    }

    /** The result of {@link #touchIfDue}: {@code status} is {@code skipped}, {@code touched} or {@code ended} (with {@code reason}). */
    public record TouchResult(String status, long lastTouchedAt, String idleExpiresAt, String reason) {
    }

    /** {@code 0} asks to confirm a sign-in unless the user authenticated in the last this many seconds. */
    public static final int CONFIRM_NOW_WINDOW_SECONDS = 60;
    static final int FLOW_MAX_AGE_SECONDS = 600;
    private static final String SEALED_PREFIX = "s1.";
    private static final int MAX_FORWARDED_VALUE = 512;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder B64U = Base64.getUrlEncoder().withoutPadding();

    private final RpConfig c;
    private final ObjectMapper json = new ObjectMapper();
    private final javax.crypto.SecretKey flowKey;
    private final javax.crypto.SecretKey sessionKey;
    private final Map<String, Object[]> discoveryCache = new ConcurrentHashMap<>();
    private final Map<String, JwksCache> jwks = new ConcurrentHashMap<>();

    public RelyingParty(RpConfig config) {
        this.c = config;
        this.flowKey = hkdfKey(config.secret, "atlas-oidc-flow-cookie");
        this.sessionKey = hkdfKey(config.secret, "atlas-rp-session");
    }

    /** Whether {@code value} is a path on this site: a single leading slash, no backslash or control characters. */
    public static boolean isSafeReturnTo(String value) {
        return value != null && value.matches("^/(?![/\\\\])[^\\\\\\u0000-\\u001f\\u007f]*$");
    }

    /** The configured callback URL. */
    public String redirectUri() {
        return c.redirectUri;
    }

    /** Where a sign-in with no return path lands. */
    public String afterSignInPath() {
        return c.afterSignInPath;
    }

    // ---- start ---------------------------------------------------------------

    /**
     * Begin sign-in from a request's query: {@code prompt}, {@code login_hint}, {@code ui_locales}, {@code max_age},
     * {@code organization} and {@code return_to} are passed through, as are the configured forward params.
     */
    public Redirect start(Map<String, String> query) {
        return start(query, new StartOptions());
    }

    /** As {@link #start(Map)}; {@code overrides} win over the query. */
    public Redirect start(Map<String, String> query, StartOptions overrides) {
        Map<String, String> q = query == null ? Map.of() : query;
        StartOptions o = new StartOptions();
        o.prompt = q.get("prompt");
        o.loginHint = q.get("login_hint");
        o.uiLocales = q.get("ui_locales");
        o.organization = q.get("organization");
        if (q.get("return_to") != null && isSafeReturnTo(q.get("return_to"))) {
            o.returnTo = q.get("return_to");
        }
        if (q.get("max_age") != null && q.get("max_age").matches("^\\d{1,9}$")) {
            o.maxAge = Integer.parseInt(q.get("max_age"));
        }
        for (String name : c.forwardParams) {
            String v = q.get(name);
            if (v != null && !v.isEmpty() && v.length() <= MAX_FORWARDED_VALUE) {
                o.extraParams.put(name, v);
            }
        }
        if (overrides.prompt != null) o.prompt = overrides.prompt;
        if (overrides.loginHint != null) o.loginHint = overrides.loginHint;
        if (overrides.uiLocales != null) o.uiLocales = overrides.uiLocales;
        if (overrides.maxAge != null) o.maxAge = overrides.maxAge;
        if (overrides.organization != null) o.organization = overrides.organization;
        if (overrides.returnTo != null) o.returnTo = overrides.returnTo;
        if (overrides.expectedSub != null) o.expectedSub = overrides.expectedSub;
        o.scope = overrides.scope != null ? overrides.scope : c.scope;
        o.extraParams.putAll(overrides.extraParams);
        return begin(o);
    }

    private Redirect begin(StartOptions o) {
        if (o.returnTo != null && !isSafeReturnTo(o.returnTo)) {
            throw new IllegalArgumentException("oidc: returnTo must be a path on this site, starting with a single '/'.");
        }
        Discovery d = discover();
        String state = randomToken();
        String nonce = randomToken();
        String verifier = randomToken();
        String challenge = B64U.encodeToString(sha256(verifier.getBytes(StandardCharsets.US_ASCII)));
        Map<String, String> params = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : o.extraParams.entrySet()) {
            if (!RpConfig.NOT_FORWARDABLE.contains(e.getKey()) && e.getValue() != null) {
                params.put(e.getKey(), e.getValue());
            }
        }
        params.put("response_type", "code");
        params.put("client_id", c.clientId);
        params.put("redirect_uri", c.redirectUri);
        params.put("scope", o.scope == null || o.scope.isBlank() ? "openid profile email" : o.scope);
        params.put("state", state);
        params.put("nonce", nonce);
        params.put("code_challenge", challenge);
        params.put("code_challenge_method", "S256");
        if (o.prompt != null) params.put("prompt", o.prompt);
        if (o.loginHint != null) params.put("login_hint", o.loginHint);
        if (o.organization != null) params.put("organization", o.organization);
        if (o.uiLocales != null) params.put("ui_locales", o.uiLocales);
        if (o.maxAge != null) {
            if (o.maxAge < 0) {
                throw new IllegalArgumentException("oidc: maxAge must be a whole number of seconds, 0 or more.");
            }
            params.put("max_age", String.valueOf(o.maxAge));
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("s", state);
        payload.put("n", nonce);
        payload.put("v", verifier);
        payload.put("r", c.redirectUri);
        payload.put("i", c.issuer);
        payload.put("c", c.clientId);
        payload.put("t", c.now.getAsLong() / 1000);
        if (o.maxAge != null) payload.put("m", o.maxAge);
        if (o.returnTo != null) payload.put("u", o.returnTo);
        if (o.expectedSub != null && !o.expectedSub.isEmpty()) payload.put("x", o.expectedSub);
        URI redirect = URI.create(c.redirectUri);
        String cookie = cookie(c.cookieName, seal(payload), redirect.getPath(), FLOW_MAX_AGE_SECONDS, "https".equals(redirect.getScheme()));
        return new Redirect(d.authorizationEndpoint + (d.authorizationEndpoint.contains("?") ? "&" : "?") + form(params), List.of(cookie));
    }

    /**
     * Step-up: null when {@code authTime} (epoch seconds) is within {@code maxAgeSeconds}; otherwise a redirect asking the
     * same user to authenticate again ({@code max_age}, plus {@code prompt=login} for 0). {@code 0} asks unless the user
     * authenticated in the last {@link #CONFIRM_NOW_WINDOW_SECONDS} seconds.
     */
    public Redirect requireRecentAuth(String sub, Long authTime, int maxAgeSeconds, String returnTo) {
        if (maxAgeSeconds < 0 || sub == null || sub.isEmpty()) {
            throw new IllegalArgumentException("requireRecentAuth: maxAgeSeconds must be 0 or more and sub is required.");
        }
        long allowed = maxAgeSeconds == 0 ? CONFIRM_NOW_WINDOW_SECONDS : maxAgeSeconds;
        if (authTime != null && c.now.getAsLong() / 1000.0 - authTime <= allowed) {
            return null;
        }
        StartOptions o = new StartOptions();
        o.scope = c.scope;
        o.maxAge = maxAgeSeconds;
        o.expectedSub = sub;
        if (maxAgeSeconds == 0) {
            o.prompt = "login";
        }
        if (returnTo != null && isSafeReturnTo(returnTo)) {
            o.returnTo = returnTo;
        }
        return begin(o);
    }

    // ---- callback ------------------------------------------------------------

    /**
     * Finish sign-in: check {@code state}, exchange the code (PKCE and client authentication), verify the ID token.
     * Throws {@link OidcException} on any failure; send its {@code clearCookie()} on your error response.
     *
     * @param query  the callback's query parameters
     * @param cookie the request's {@code Cookie} header
     */
    public CallbackResult callback(Map<String, String> query, String cookie) {
        URI redirect = URI.create(c.redirectUri);
        String clear = cookie(c.cookieName, "", redirect.getPath(), 0, "https".equals(redirect.getScheme()));
        try {
            return runCallback(query == null ? Map.of() : query, cookie, clear);
        } catch (OidcException e) {
            e.clearCookie(clear);
            throw e;
        } catch (RuntimeException e) {
            OidcException wrapped = new OidcException("invalid_request", String.valueOf(e.getMessage()), e);
            wrapped.clearCookie(clear);
            throw wrapped;
        }
    }

    private CallbackResult runCallback(Map<String, String> query, String cookieHeader, String clear) {
        String raw = readCookie(cookieHeader, c.cookieName);
        if (raw == null) {
            throw new OidcException("state_mismatch", "The sign-in flow cookie is missing or expired. Start sign-in again.");
        }
        Map<String, Object> flow = open(raw);
        if (flow == null) {
            throw new OidcException("state_mismatch", "The sign-in flow cookie could not be read. Start sign-in again.");
        }
        String state = query.get("state");
        if (state == null || !constantTimeEquals(state, String.valueOf(flow.get("s")))) {
            throw new OidcException("state_mismatch", "The `state` returned to the callback does not match this browser's sign-in.");
        }
        long nowSec = c.now.getAsLong() / 1000;
        if (nowSec - ((Number) flow.get("t")).longValue() > FLOW_MAX_AGE_SECONDS) {
            throw new OidcException("state_mismatch", "This sign-in took longer than 10 minutes. Start sign-in again.");
        }
        if (!c.issuer.equals(flow.get("i")) || !c.clientId.equals(flow.get("c")) || !c.redirectUri.equals(flow.get("r"))) {
            throw new OidcException("state_mismatch", "This sign-in was started for a different issuer, client or redirect URI.");
        }
        String returnedIss = query.get("iss");
        if (returnedIss != null && !returnedIss.replaceAll("/+$", "").equals(c.issuer)) {
            throw new OidcException("issuer_mismatch", "The callback came from " + returnedIss + ", not " + c.issuer + ".");
        }
        String oauthError = query.get("error");
        if (oauthError != null && !oauthError.isEmpty()) {
            String description = query.get("error_description");
            throw new OidcException("access_denied", "Sign-in did not complete: " + oauthError + (description != null ? " (" + description + ")" : "") + ".");
        }
        String code = query.get("code");
        if (code == null || code.isEmpty()) {
            throw new OidcException("invalid_request", "The callback has no `code` parameter.");
        }
        Discovery d = discover();
        JsonNode tokens = exchange(d, code, String.valueOf(flow.get("v")));
        String idToken = tokens.path("id_token").asText(null);
        String accessToken = tokens.path("access_token").asText(null);
        if (idToken == null || accessToken == null) {
            throw new OidcException("token_exchange_failed", "The token endpoint answered without an id_token and access_token.");
        }
        JWTClaimsSet claims = verifyIdToken(idToken, d, String.valueOf(flow.get("n")), accessToken);
        String sub = claims.getSubject();
        Long authTime = number(claims, "auth_time");
        if (flow.get("m") != null) {
            long maxAge = ((Number) flow.get("m")).longValue();
            if (authTime == null || nowSec - authTime > maxAge + 60) {
                throw new OidcException("reauthentication_required", "The sign-in is older than the " + maxAge + " seconds this request allowed (max_age). Ask the user to sign in again.");
            }
        }
        if (flow.get("x") != null && !constantTimeEquals(sub, String.valueOf(flow.get("x")))) {
            throw new OidcException("subject_mismatch", "A different user signed in than the one asked to confirm it is them.");
        }
        long expiresIn = tokens.path("expires_in").asLong(0);
        long expiresAt = expiresIn > 0 ? c.now.getAsLong() + expiresIn * 1000 : claims.getExpirationTime().getTime();
        Map<String, Object> all = claims.getClaims();
        String sid = all.get("sid") instanceof String s && !s.isEmpty() ? s : null;
        String orgId = all.get("org_id") instanceof String s ? s : null;
        String orgRole = all.get("org_role") instanceof String s ? s : null;
        User user = new User(sub, sid, all.get("email") instanceof String s ? s : null, Boolean.TRUE.equals(all.get("email_verified")),
                all.get("name") instanceof String s ? s : null, all.get("external_id") instanceof String s && !s.isEmpty() ? s : null,
                orgId, orgRole, authTime);
        String returnTo = flow.get("u") instanceof String u && isSafeReturnTo(u) ? u : null;
        return new CallbackResult(user, new Tokens(idToken, accessToken, tokens.hasNonNull("refresh_token") ? tokens.get("refresh_token").asText() : null, expiresAt),
                all, returnTo, clear);
    }

    private JsonNode exchange(Discovery d, String code, String verifier) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        body.put("redirect_uri", c.redirectUri);
        body.put("code_verifier", verifier);
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(d.tokenEndpoint)).timeout(Duration.ofSeconds(15))
                .header("content-type", "application/x-www-form-urlencoded").header("accept", "application/json");
        switch (c.clientAuth) {
            case CLIENT_SECRET_BASIC -> req.header("authorization", "Basic " + Base64.getEncoder().encodeToString(
                    (enc(c.clientId) + ":" + enc(c.clientSecret == null ? "" : c.clientSecret)).getBytes(StandardCharsets.UTF_8)));
            case CLIENT_SECRET_POST -> {
                body.put("client_id", c.clientId);
                body.put("client_secret", c.clientSecret == null ? "" : c.clientSecret);
            }
            default -> body.put("client_id", c.clientId);
        }
        HttpResponse<String> res = send(req.POST(HttpRequest.BodyPublishers.ofString(form(body))).build());
        JsonNode node = parse(res.body());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new OidcException("token_exchange_failed", "The token endpoint answered " + res.statusCode()
                    + (node.hasNonNull("error") ? ": " + node.get("error").asText() : "") + ".");
        }
        return node;
    }

    private JWTClaimsSet verifyIdToken(String idToken, Discovery d, String nonce, String accessToken) {
        SignedJWT jwt;
        try {
            jwt = SignedJWT.parse(idToken);
        } catch (ParseException e) {
            throw invalid("it is not a JWT");
        }
        // pinned before any key lookup, so none and HS* never get near a key
        if (!SigningAlgs.allowed(jwt)) {
            throw invalid("its algorithm is " + jwt.getHeader().getAlgorithm() + ", not one of RS256, ES256, ES384, PS256, EdDSA");
        }
        com.nimbusds.jose.jwk.JWKSet keys = jwks.computeIfAbsent(d.jwksUri, u -> new JwksCache(u, c.http, c.now)).get(jwt.getHeader().getKeyID());
        if (keys == null || keys.getKeys().isEmpty()) {
            throw new OidcException("discovery_failed", "The issuer's signing keys could not be fetched from " + d.jwksUri + ".");
        }
        if (!SigningAlgs.verify(jwt, keys, jwt.getHeader().getKeyID())) {
            throw invalid("its signature does not verify");
        }
        JWTClaimsSet claims;
        try {
            claims = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw invalid("its claims are not readable");
        }
        long skewMs = 60_000;
        long now = c.now.getAsLong();
        if (!d.issuer.equals(claims.getIssuer())) {
            throw invalid("its issuer is not the configured issuer");
        }
        List<String> aud = claims.getAudience();
        if (aud == null || !aud.contains(c.clientId)) {
            throw invalid("its audience is not this client");
        }
        if (claims.getSubject() == null || claims.getIssueTime() == null || claims.getExpirationTime() == null) {
            throw invalid("it lacks sub, iat or exp");
        }
        if (claims.getExpirationTime().getTime() + skewMs < now) {
            throw invalid("it has expired");
        }
        if (claims.getIssueTime().getTime() - skewMs > now) {
            throw invalid("it was issued in the future");
        }
        Object azp = claims.getClaim("azp");
        if (aud.size() > 1 && azp == null) {
            throw invalid("it has several audiences and no azp");
        }
        if (azp != null && !c.clientId.equals(azp)) {
            throw invalid("its azp is " + azp + ", not this client");
        }
        if (!(claims.getClaim("nonce") instanceof String n) || !constantTimeEquals(n, nonce)) {
            throw invalid("its nonce does not match this sign-in");
        }
        if (claims.getClaim("at_hash") instanceof String atHash) {
            byte[] digest = sha256(accessToken.getBytes(StandardCharsets.US_ASCII));
            if (!constantTimeEquals(B64U.encodeToString(Arrays.copyOf(digest, 16)), atHash)) {
                throw invalid("its at_hash does not match the access token");
            }
        }
        return claims;
    }

    private static OidcException invalid(String why) {
        return new OidcException("invalid_token", "The ID token was refused: " + why + ".");
    }

    // ---- keep-alive and sign-out -----------------------------------------------

    /**
     * Keep the Atlas session alive while the user is active, at most once per {@code touchIntervalMs} per session. Call it on
     * each authenticated request with the {@code sid} and the time you last touched (0 or null at first). With a
     * {@code secretKey} it touches {@code /v1/sessions/{id}/touch}; without one but with a {@code clientSecret} it touches this
     * app's own sign-in ({@code POST {issuer}/oauth2/session/touch}). Status {@code ended} (reasons {@code revoked},
     * {@code idle_expired}, {@code expired}, {@code not_found}) means end your own session too.
     */
    public TouchResult touchIfDue(String sid, Long lastTouchedAt) {
        long now = c.now.getAsLong();
        if (lastTouchedAt != null && now - lastTouchedAt < c.touchIntervalMs && now >= lastTouchedAt) {
            return new TouchResult("skipped", lastTouchedAt, null, null);
        }
        if (c.secretKey == null || c.secretKey.isEmpty()) {
            HttpResponse<String> res = appSessionCall(sid, "touch", "touchIfDue");
            JsonNode body = parse(res.body());
            if (res.statusCode() / 100 == 2) {
                JsonNode idle = body.get("idle_expires_at");
                String at = idle == null || idle.isNull() ? null : idle.isNumber() ? java.time.Instant.ofEpochMilli(idle.asLong()).toString() : idle.asText();
                return new TouchResult("touched", now, at, null);
            }
            if (res.statusCode() == 404 && "session_not_found".equals(body.path("error").asText())) {
                return new TouchResult("ended", now, null, "not_found");
            }
            throw oauthFailure(res, body);
        }
        HttpResponse<String> res = sessionCall(sid, "touch");
        JsonNode body = parse(res.body());
        if (res.statusCode() / 100 == 2) {
            return new TouchResult("touched", now, body.hasNonNull("idle_expires_at") ? body.get("idle_expires_at").asText() : null, null);
        }
        if (res.statusCode() == 404) {
            return new TouchResult("ended", now, null, "not_found");
        }
        if (res.statusCode() == 409) {
            String code = firstCode(body);
            return new TouchResult("ended", now, null, "SESSION_REVOKED".equals(code) ? "revoked" : "SESSION_IDLE_EXPIRED".equals(code) ? "idle_expired" : "expired");
        }
        throw apiFailure(res, body);
    }

    /** End the Atlas session server-side; every app signed in through it gets a back-channel logout. Returns {@code revoked} or {@code not_found}. */
    public String revokeSession(String sid) {
        HttpResponse<String> res = sessionCall(sid, "revoke");
        if (res.statusCode() / 100 == 2) {
            return "revoked";
        }
        if (res.statusCode() == 404) {
            return "not_found";
        }
        throw apiFailure(res, parse(res.body()));
    }

    /** End THIS app's sign-in under {@code sid} with the app's client credentials. Returns {@code ended} or {@code not_found}. */
    public String endAppSession(String sid) {
        HttpResponse<String> res = appSessionCall(sid, "end", "endAppSession");
        JsonNode body = parse(res.body());
        if (res.statusCode() / 100 == 2) {
            return "ended";
        }
        if (res.statusCode() == 404 && "session_not_found".equals(body.path("error").asText())) {
            return "not_found";
        }
        throw oauthFailure(res, body);
    }

    /**
     * Sign-out: with a {@code sid} and a {@code secretKey} revokes the Atlas session server-side first, then redirects to
     * {@code /oauth2/logout}. With {@code serverSignOut} one call ends this app's grants and the Atlas session and the redirect
     * goes straight to the post-logout URI. End your own session (clear your cookie on this response) as well.
     *
     * @param idToken the ID token from sign-in, so Atlas can end the session without asking (may be null)
     * @param cookies {@code Set-Cookie} values to send on the redirect, for example the one that clears your session cookie
     */
    public Redirect signOut(String sid, String idToken, String postLogoutRedirectUri, String state, List<String> cookies) {
        List<String> setCookies = cookies == null ? List.of() : cookies;
        String after = postLogoutRedirectUri != null ? postLogoutRedirectUri : c.postLogoutRedirectUri;
        boolean hasKey = c.secretKey != null && !c.secretKey.isEmpty();
        if (c.serverSignOut && sid != null && hasKey) {
            HttpRequest req = HttpRequest.newBuilder(URI.create(c.apiUrl + "/v1/oauth_clients/" + enc(c.clientId) + "/logout"))
                    .timeout(Duration.ofSeconds(15)).header("authorization", "Bearer " + c.secretKey).header("accept", "application/json")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"sid\":" + quote(sid) + "}")).build();
            HttpResponse<String> res = send(req);
            if (res.statusCode() / 100 != 2 && res.statusCode() != 404) {
                throw apiFailure(res, parse(res.body()));
            }
            String target = after == null ? "/" : after;
            if (state != null) {
                target += (target.contains("?") ? "&" : "?") + "state=" + enc(state);
            }
            return new Redirect(target, setCookies);
        }
        if (sid != null && hasKey) {
            revokeSession(sid);
        }
        Discovery d = discover();
        if (d.endSessionEndpoint == null) {
            throw new OidcException("discovery_failed", "The issuer does not advertise an end_session_endpoint.");
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("client_id", c.clientId);
        if (idToken != null) params.put("id_token_hint", idToken);
        if (after != null) params.put("post_logout_redirect_uri", after);
        if (state != null) params.put("state", state);
        return new Redirect(d.endSessionEndpoint + (d.endSessionEndpoint.contains("?") ? "&" : "?") + form(params), setCookies);
    }

    private HttpResponse<String> sessionCall(String sid, String action) {
        if (c.secretKey == null || c.secretKey.isEmpty()) {
            throw new IllegalStateException(("touch".equals(action) ? "touchIfDue" : "revokeSession") + ": secretKey is not configured.");
        }
        if (sid == null || sid.isEmpty()) {
            throw new IllegalArgumentException("A session id (sid) is required.");
        }
        return send(HttpRequest.newBuilder(URI.create(c.apiUrl + "/v1/sessions/" + enc(sid) + "/" + action)).timeout(Duration.ofSeconds(15))
                .header("authorization", "Bearer " + c.secretKey).header("accept", "application/json").POST(HttpRequest.BodyPublishers.noBody()).build());
    }

    private HttpResponse<String> appSessionCall(String sid, String action, String caller) {
        if (c.clientAuth == RpConfig.ClientAuth.NONE || c.clientSecret == null) {
            throw new IllegalStateException(caller + ": configure clientSecret (the app's client credentials)" + ("touch".equals(action) ? " or secretKey" : "")
                    + "; a public client cannot call it.");
        }
        if (sid == null || sid.isEmpty()) {
            throw new IllegalArgumentException("A session id (sid) is required.");
        }
        Map<String, String> body = new LinkedHashMap<>();
        body.put("sid", sid);
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(c.issuer + "/oauth2/session/" + action)).timeout(Duration.ofSeconds(15))
                .header("content-type", "application/x-www-form-urlencoded").header("accept", "application/json");
        if (c.clientAuth == RpConfig.ClientAuth.CLIENT_SECRET_BASIC) {
            req.header("authorization", "Basic " + Base64.getEncoder().encodeToString((enc(c.clientId) + ":" + enc(c.clientSecret)).getBytes(StandardCharsets.UTF_8)));
        } else {
            body.put("client_id", c.clientId);
            body.put("client_secret", c.clientSecret);
        }
        return send(req.POST(HttpRequest.BodyPublishers.ofString(form(body))).build());
    }

    private static AtlasApiException apiFailure(HttpResponse<String> res, JsonNode body) {
        List<ErrorItem> errors = new ArrayList<>();
        for (JsonNode e : body.path("errors")) {
            errors.add(new ErrorItem(e.path("code").asText("UNKNOWN"), e.path("message").asText("request failed"), null, null));
        }
        if (errors.isEmpty()) {
            errors.add(new ErrorItem("UNKNOWN", "Atlas API request failed with status " + res.statusCode(), null, null));
        }
        return AtlasApiException.of(res.statusCode(), errors);
    }

    private static AtlasApiException oauthFailure(HttpResponse<String> res, JsonNode body) {
        return AtlasApiException.of(res.statusCode(), List.of(new ErrorItem(body.path("error").asText("request_failed"),
                body.path("error_description").asText("The issuer answered " + res.statusCode() + "."), null, null)));
    }

    private static String firstCode(JsonNode body) {
        JsonNode errors = body.path("errors");
        return errors.isArray() && errors.size() > 0 ? errors.get(0).path("code").asText(null) : null;
    }

    // ---- discovery ---------------------------------------------------------------

    private record Discovery(String issuer, String authorizationEndpoint, String tokenEndpoint, String jwksUri, String endSessionEndpoint) {
    }

    private Discovery discover() {
        Object[] hit = discoveryCache.get(c.issuer);
        long now = c.now.getAsLong();
        if (hit != null && now < (Long) hit[1]) {
            if (hit[0] instanceof Discovery d) {
                return d;
            }
            throw (OidcException) hit[0];
        }
        String url = c.issuer + "/.well-known/openid-configuration";
        try {
            HttpResponse<String> res = send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).header("accept", "application/json").GET().build());
            if (res.statusCode() != 200) {
                throw new OidcException("discovery_failed", "OIDC discovery at " + url + " failed: HTTP " + res.statusCode() + ".");
            }
            JsonNode doc = parse(res.body());
            for (String f : List.of("authorization_endpoint", "token_endpoint", "jwks_uri")) {
                if (!doc.path(f).isTextual()) {
                    throw new OidcException("discovery_failed", "OIDC discovery at " + url + " failed: the document has no " + f + ".");
                }
            }
            String docIssuer = doc.path("issuer").asText("").replaceAll("/+$", "");
            if (!docIssuer.equals(c.issuer)) {
                throw new OidcException("discovery_failed", "OIDC discovery at " + url + " failed: its issuer is " + docIssuer + ".");
            }
            Discovery d = new Discovery(docIssuer, doc.get("authorization_endpoint").asText(), doc.get("token_endpoint").asText(), doc.get("jwks_uri").asText(),
                    doc.hasNonNull("end_session_endpoint") ? doc.get("end_session_endpoint").asText() : null);
            discoveryCache.put(c.issuer, new Object[]{d, now + 3_600_000L});
            return d;
        } catch (OidcException e) {
            discoveryCache.put(c.issuer, new Object[]{e, now + 60_000L}); // a failure is remembered for a minute: no request flood
            throw e;
        }
    }

    // ---- sealing, cookies, small helpers ---------------------------------------------

    private static javax.crypto.SecretKey hkdfKey(String secret, String salt) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("The OIDC flow-cookie secret must be at least 32 characters.");
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] prk = mac.doFinal(secret.getBytes(StandardCharsets.UTF_8));
            mac.init(new SecretKeySpec(prk, "HmacSHA256"));
            mac.update("v1".getBytes(StandardCharsets.UTF_8));
            mac.update((byte) 1);
            return new SecretKeySpec(mac.doFinal(), "AES");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private String seal(Map<String, Object> payload) {
        return seal(flowKey, payload);
    }

    /** Seal a small map for a session cookie (AES-256-GCM, a key separate from the flow cookie's). */
    String sealSession(Map<String, Object> payload) {
        return seal(sessionKey, payload);
    }

    Map<String, Object> openSession(String value) {
        return open(sessionKey, value);
    }

    private String seal(javax.crypto.SecretKey flowKey, Map<String, Object> payload) {
        try {
            byte[] iv = new byte[12];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, flowKey, new GCMParameterSpec(128, iv));
            byte[] sealed = cipher.doFinal(json.writeValueAsBytes(payload));
            return SEALED_PREFIX + B64U.encodeToString(iv) + "." + B64U.encodeToString(sealed);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, Object> open(String value) {
        return open(flowKey, value);
    }

    private Map<String, Object> open(javax.crypto.SecretKey flowKey, String value) {
        try {
            if (!value.startsWith(SEALED_PREFIX)) {
                return null; // an unsealed cookie is a downgrade: refuse it
            }
            String[] parts = value.substring(SEALED_PREFIX.length()).split("\\.");
            if (parts.length != 2) {
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, flowKey, new GCMParameterSpec(128, Base64.getUrlDecoder().decode(parts[0])));
            return json.readValue(cipher.doFinal(Base64.getUrlDecoder().decode(parts[1])), new TypeReference<Map<String, Object>>() {
            });
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            return null;
        }
    }

    /** A {@code Set-Cookie} value: HttpOnly, SameSite=Lax, Secure on https. */
    static String cookie(String name, String value, String path, int maxAge, boolean secure) {
        StringBuilder sb = new StringBuilder(name).append('=').append(value).append("; Path=").append(path == null || path.isEmpty() ? "/" : path)
                .append("; Max-Age=").append(maxAge).append("; HttpOnly; SameSite=Lax");
        if (maxAge == 0) {
            sb.append("; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
        }
        if (secure) {
            sb.append("; Secure");
        }
        return sb.toString();
    }

    static String readCookie(String header, String name) {
        if (header == null) {
            return null;
        }
        for (String part : header.split(";")) {
            String p = part.trim();
            if (p.startsWith(name + "=")) {
                return p.substring(name.length() + 1);
            }
        }
        return null;
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return c.http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new OidcException("discovery_failed", "Request to " + request.uri() + " failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OidcException("discovery_failed", "Request to " + request.uri() + " was interrupted", e);
        }
    }

    private JsonNode parse(String text) {
        try {
            return text == null || text.isEmpty() ? json.createObjectNode() : json.readTree(text);
        } catch (IOException e) {
            return json.createObjectNode();
        }
    }

    private static Long number(JWTClaimsSet claims, String name) {
        return claims.getClaim(name) instanceof Number n ? Long.valueOf(n.longValue()) : null;
    }

    private static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String form(Map<String, String> params) {
        List<String> parts = new ArrayList<>();
        params.forEach((k, v) -> parts.add(enc(k) + "=" + enc(v)));
        return String.join("&", parts);
    }

    private static String randomToken() {
        byte[] b = new byte[32];
        RANDOM.nextBytes(b);
        return B64U.encodeToString(b);
    }

    private static byte[] sha256(byte[] in) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(in);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
