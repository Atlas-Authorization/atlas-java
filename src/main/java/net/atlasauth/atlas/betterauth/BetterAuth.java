package net.atlasauth.atlas.betterauth;

import net.atlasauth.atlas.AtlasApiException;
import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.AtlasConnectionException;
import net.atlasauth.atlas.ErrorItem;
import net.atlasauth.atlas.models.BetterAuthExchangeResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Moving from Better Auth to Atlas without signing anyone out.
 *
 * <p>During the migration window a returning user still carries their Better Auth session cookie
 * ({@code better-auth.session_token}, {@code __Secure-} prefixed over https). It is HttpOnly, so only your
 * server can read it. The bridge reads it, exchanges it ONCE with Atlas through the Backend API
 * ({@code POST /v1/migrations/better_auth/exchange}, secret key, forwarding the end user's IP and user
 * agent), sets the Atlas session as your own first-party cookies and clears the Better Auth cookies. Atlas
 * finds the session in your import (or asks your old app, with the live fallback on) and runs the normal
 * sign-in path on it, MFA policy included.
 *
 * <p>{@link SessionBridge} turns a request's cookies into a {@link BridgeDecision}; the Spring
 * {@code AtlasAuthenticationFilter} renders it with {@code atlas.better-auth-bridge.enabled=true}.
 */
public final class BetterAuth {

    /** Better Auth's default cookie prefix ({@code advanced.cookiePrefix}). */
    public static final String COOKIE_PREFIX = "better-auth";

    /** Refusal outcomes Atlas reports in {@code meta.bridge_outcome}. */
    public static final Set<String> KNOWN_OUTCOMES = Set.of(
            "closed", "invalid", "signature_invalid", "expired", "reused", "user_unavailable", "rate_limited");

    /** Refusals that say the Better Auth session will never exchange: its cookies are cleared. */
    public static final Set<String> FINAL_OUTCOMES = Set.of(
            "invalid", "signature_invalid", "expired", "reused", "user_unavailable");

    private BetterAuth() {
    }

    /** The session cookie names, the {@code __Secure-} name first. */
    public static List<String> sessionCookieNames(String prefix) {
        return List.of("__Secure-" + prefix + ".session_token", prefix + ".session_token");
    }

    /** Every Better Auth cookie the bridge clears ({@code session_token}, {@code session_data}, ...; both prefixes). */
    public static List<String> allCookieNames(String prefix) {
        List<String> base = new ArrayList<>();
        for (String n : List.of("session_token", "session_data", "account_data", "dont_remember")) {
            base.add(prefix + "." + n);
        }
        List<String> out = new ArrayList<>();
        for (String n : base) {
            out.add("__Secure-" + n);
        }
        out.addAll(base);
        return out;
    }

    /** Parse a {@code Cookie} header (the first occurrence of a name wins). */
    public static Map<String, String> parseCookieHeader(String header) {
        Map<String, String> out = new LinkedHashMap<>();
        if (header == null || header.isEmpty()) {
            return out;
        }
        for (String part : header.split(";")) {
            String p = part.trim();
            int eq = p.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            out.putIfAbsent(p.substring(0, eq), p.substring(eq + 1));
        }
        return out;
    }

    /**
     * The raw Better Auth session cookie value, the {@code __Secure-} name first; null when absent.
     * {@code cookieName} (nullable) overrides the name when the old app set
     * {@code advanced.cookies.session_token.name}.
     */
    public static String readSessionCookie(Map<String, String> cookies, String cookiePrefix, String cookieName) {
        if (cookies == null) {
            return null;
        }
        List<String> names = cookieName != null && !cookieName.isEmpty()
                ? List.of("__Secure-" + cookieName, cookieName)
                : sessionCookieNames(cookiePrefix != null ? cookiePrefix : COOKIE_PREFIX);
        for (String name : names) {
            String v = cookies.get(name);
            if (v != null && !v.isEmpty()) {
                return v;
            }
        }
        return null;
    }

    /** {@link #readSessionCookie(Map, String, String)} over a {@code Cookie} header. */
    public static String readSessionCookie(String cookieHeader) {
        return readSessionCookie(parseCookieHeader(cookieHeader), COOKIE_PREFIX, null);
    }

    /**
     * Exchange a Better Auth session (the raw cookie value or the bare token) for an Atlas session over the
     * Backend API. Never throws for a refusal or a transport failure: those are {@code REFUSED}
     * ({@code unavailable} when it is not a verdict on the session), so the caller keeps the Better Auth
     * cookie and tries again later. Pass the END USER's {@code clientIp} and {@code userAgent}.
     */
    public static ExchangeResult exchange(AtlasClient client, String token, String clientIp, String userAgent,
                                          Boolean remember) {
        BetterAuthExchangeResponse data;
        try {
            data = client.betterAuthBridge().exchange(token, clientIp, userAgent, remember);
        } catch (AtlasApiException e) {
            return classifyError(e);
        } catch (AtlasConnectionException e) {
            return ExchangeResult.refused("unavailable", 0);
        }
        return classifySuccess(data);
    }

    /** {@link #exchange(AtlasClient, String, String, String, Boolean)} with the default {@code remember}. */
    public static ExchangeResult exchange(AtlasClient client, String token, String clientIp, String userAgent) {
        return exchange(client, token, clientIp, userAgent, null);
    }

    public static ExchangeResult classifySuccess(BetterAuthExchangeResponse data) {
        if (data != null) {
            if ("session_tokens".equals(data.object()) && notEmpty(data.jwt()) && notEmpty(data.refreshToken())) {
                return ExchangeResult.session(data.jwt(), data.refreshToken(),
                        data.sessionId() != null ? data.sessionId() : "",
                        data.userId() != null ? data.userId() : "",
                        data.expiresIn() != null ? data.expiresIn() : 0);
            }
            if ("sign_in_attempt".equals(data.object()) && notEmpty(data.id())
                    && ("needs_second_factor".equals(data.status()) || "needs_mfa_enrollment".equals(data.status()))) {
                return ExchangeResult.needsMfa(data.id(), data.status());
            }
        }
        return ExchangeResult.refused("unavailable", 200);
    }

    public static ExchangeResult classifyError(AtlasApiException e) {
        if (e.status() == 429) {
            return ExchangeResult.refused("rate_limited", 429);
        }
        List<ErrorItem> errors = e.errors();
        Object outcome = null;
        if (!errors.isEmpty() && errors.get(0).meta() != null) {
            outcome = errors.get(0).meta().get("bridge_outcome");
        }
        if (outcome instanceof String s && KNOWN_OUTCOMES.contains(s)) {
            return ExchangeResult.refused(s, e.status());
        }
        // Anything else (a 5xx, a guard refusal, an unparsable body) is not a verdict on the Better Auth
        // session: keep it and retry later.
        return ExchangeResult.refused("unavailable", e.status());
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
