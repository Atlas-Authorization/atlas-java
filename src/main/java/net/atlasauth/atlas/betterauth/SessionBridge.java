package net.atlasauth.atlas.betterauth;

import net.atlasauth.atlas.AtlasClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The bridge as a function of a request: call it before your normal auth check. It does nothing (no
 * network) for a request without a Better Auth cookie or with an Atlas session ({@code __session} /
 * {@code __atlas_rt}) already present.
 *
 * <pre>{@code
 * SessionBridge bridge = SessionBridge.builder(AtlasClient.of(System.getenv("ATLAS_SECRET_KEY"))).build();
 * BridgeDecision d = bridge.decide(BetterAuth.parseCookieHeader(cookieHeader), clientIp, userAgent);
 * }</pre>
 */
public final class SessionBridge {

    /** The exchange call (overridable for tests). */
    @FunctionalInterface
    public interface Exchanger {
        ExchangeResult exchange(AtlasClient client, String token, String clientIp, String userAgent);
    }

    private final AtlasClient client;
    private final String cookiePrefix;
    private final String cookieName;
    private final String betterAuthCookieDomain;
    private final boolean secure;
    private final Exchanger exchanger;

    private SessionBridge(Builder b) {
        if (b.client == null) {
            throw new IllegalArgumentException("SessionBridge needs an AtlasClient on the instance secret key.");
        }
        this.client = b.client;
        this.cookiePrefix = b.cookiePrefix != null && !b.cookiePrefix.isEmpty() ? b.cookiePrefix : BetterAuth.COOKIE_PREFIX;
        this.cookieName = b.cookieName;
        this.betterAuthCookieDomain = b.betterAuthCookieDomain;
        this.secure = b.secure;
        this.exchanger = b.exchanger != null ? b.exchanger : BetterAuth::exchange;
    }

    public static Builder builder(AtlasClient client) {
        return new Builder(client);
    }

    /**
     * Decide for one request.
     *
     * @param cookies   the request's cookies by name
     * @param clientIp  the end user's IP as your stack resolved it (nullable)
     * @param userAgent the end user's user agent (nullable)
     */
    public BridgeDecision decide(Map<String, String> cookies, String clientIp, String userAgent) {
        String token = BetterAuth.readSessionCookie(cookies, cookiePrefix, cookieName);
        // Already on Atlas: leave the old cookie for the old app's own expiry.
        if (token == null || notEmpty(cookies.get("__session")) || notEmpty(cookies.get("__atlas_rt"))) {
            return BridgeDecision.none();
        }
        return decideFor(exchanger.exchange(client, token, clientIp, userAgent));
    }

    /** The decision for an exchange outcome. */
    public BridgeDecision decideFor(ExchangeResult result) {
        if (result.kind() == ExchangeResult.Kind.SESSION) {
            List<BridgeCookie> cookies = new ArrayList<>();
            cookies.add(new BridgeCookie("__session", result.jwt(), "/", null, false, "Lax", secure, null));
            cookies.add(new BridgeCookie("__atlas_rt", result.refreshToken(), "/", null, true, "Lax", secure, null));
            cookies.addAll(clearCookies());
            return new BridgeDecision(BridgeDecision.Action.SIGNED_IN, cookies, result, "", "", null);
        }
        if (result.kind() == ExchangeResult.Kind.NEEDS_MFA) {
            return new BridgeDecision(BridgeDecision.Action.NEEDS_MFA, clearCookies(), null, result.attemptId(),
                    result.status(), null);
        }
        String outcome = result.outcome() != null ? result.outcome() : "unavailable";
        return new BridgeDecision(BridgeDecision.Action.REFUSED,
                BetterAuth.FINAL_OUTCOMES.contains(outcome) ? clearCookies() : List.of(), null, "", "", outcome);
    }

    /** Expired cookies for every Better Auth cookie name. */
    public List<BridgeCookie> clearCookies() {
        List<String> names = new ArrayList<>(BetterAuth.allCookieNames(cookiePrefix));
        if (cookieName != null && !cookieName.isEmpty()) {
            names.add(cookieName);
            names.add("__Secure-" + cookieName);
        }
        List<BridgeCookie> out = new ArrayList<>();
        for (String n : names) {
            // A `__Secure-` cookie can only be overwritten by a Secure Set-Cookie.
            out.add(new BridgeCookie(n, "", "/", betterAuthCookieDomain, false, null, n.startsWith("__Secure-"), 0));
        }
        return out;
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static final class Builder {
        private final AtlasClient client;
        private String cookiePrefix = BetterAuth.COOKIE_PREFIX;
        private String cookieName;
        private String betterAuthCookieDomain;
        private boolean secure = true;
        private Exchanger exchanger;

        private Builder(AtlasClient client) {
            this.client = client;
        }

        /** Better Auth's {@code advanced.cookiePrefix}. */
        public Builder cookiePrefix(String v) {
            this.cookiePrefix = v;
            return this;
        }

        /** The session cookie's full name, when the old app overrode it. */
        public Builder cookieName(String v) {
            this.cookieName = v;
            return this;
        }

        /** The {@code domain} the Better Auth cookies were set on ({@code crossSubDomainCookies.domain}), so they clear. */
        public Builder betterAuthCookieDomain(String v) {
            this.betterAuthCookieDomain = v;
            return this;
        }

        /** Mark the Atlas cookies {@code Secure} (default true; false only for plain-http local development). */
        public Builder secure(boolean v) {
            this.secure = v;
            return this;
        }

        /** Override the exchange (tests). */
        public Builder exchanger(Exchanger v) {
            this.exchanger = v;
            return this;
        }

        public SessionBridge build() {
            return new SessionBridge(this);
        }
    }
}
