package net.atlasauth.atlas.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Binds the {@code atlas.*} block of a Spring Boot application's configuration.
 *
 * <pre>{@code
 * atlas:
 *   jwks-url: https://api.atlasauth.net/v1/jwks
 *   issuer: https://clerk.your-domain.com
 *   cookie-name: __session
 *   protected-paths:
 *     - /api/**
 *   # Migrating from Better Auth:
 *   secret-key: ${ATLAS_SECRET_KEY}
 *   better-auth-bridge:
 *     enabled: true
 *     sign-in-url: /sign-in
 *   legacy-issuers:
 *     - issuer: https://app.example.com
 *       jwks-url: https://app.example.com/api/auth/jwks
 *       until: 2026-12-31T00:00:00Z
 * }</pre>
 *
 * <p>{@link #jwksUrl} and {@link #issuer} are the only values
 * {@link net.atlasauth.atlas.verify.SessionVerifier} needs; the rest tune how the
 * {@link AtlasAuthenticationFilter} reads the request and which paths it guards.
 */
@ConfigurationProperties("atlas")
public class AtlasProperties {

    /** JWKS endpoint — the verifier's only required network dependency. */
    private String jwksUrl;

    /** Expected {@code iss}. An unchecked issuer would accept any Atlas instance. */
    private String issuer;

    /** Backend API secret key, for the documented {@code verifyOnline} slow path only. */
    private String secretKey;

    /** Backend API base URL, paired with {@link #secretKey} for {@code verifyOnline}. */
    private String bapiBaseUrl;

    /** Session cookie read when no {@code Authorization: Bearer} header is present. */
    private String cookieName = "__session";

    /**
     * Ant-style path patterns the filter guards (e.g. {@code /api/**}). A request
     * matching one of these without a valid session gets a 401. Empty (the
     * default) means the filter authenticates opportunistically and blocks
     * nothing — path protection is left to the application or Spring Security.
     */
    private List<String> protectedPaths = List.of();

    /** The Better Auth session bridge (off by default). Needs {@link #secretKey}. */
    private BetterAuthBridge betterAuthBridge = new BetterAuthBridge();

    /** Old issuers (Better Auth) whose JWTs the verifier also accepts until each one's {@code until}. */
    private List<LegacyIssuerProperties> legacyIssuers = List.of();

    public String getJwksUrl() {
        return jwksUrl;
    }

    public void setJwksUrl(String jwksUrl) {
        this.jwksUrl = jwksUrl;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBapiBaseUrl() {
        return bapiBaseUrl;
    }

    public void setBapiBaseUrl(String bapiBaseUrl) {
        this.bapiBaseUrl = bapiBaseUrl;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public List<String> getProtectedPaths() {
        return protectedPaths;
    }

    public void setProtectedPaths(List<String> protectedPaths) {
        this.protectedPaths = protectedPaths == null ? List.of() : List.copyOf(protectedPaths);
    }

    public BetterAuthBridge getBetterAuthBridge() {
        return betterAuthBridge;
    }

    public void setBetterAuthBridge(BetterAuthBridge betterAuthBridge) {
        this.betterAuthBridge = betterAuthBridge == null ? new BetterAuthBridge() : betterAuthBridge;
    }

    public List<LegacyIssuerProperties> getLegacyIssuers() {
        return legacyIssuers;
    }

    public void setLegacyIssuers(List<LegacyIssuerProperties> legacyIssuers) {
        this.legacyIssuers = legacyIssuers == null ? List.of() : List.copyOf(legacyIssuers);
    }

    /**
     * {@code atlas.better-auth-bridge.*}: on a GET or HEAD carrying a Better Auth session cookie (and no Atlas
     * session) the filter exchanges it once with Atlas and redirects to the same URL signed in, so moving from
     * Better Auth signs nobody out.
     */
    public static class BetterAuthBridge {
        /** Turn the bridge on. */
        private boolean enabled;
        /** Better Auth's {@code advanced.cookiePrefix}. */
        private String cookiePrefix = "better-auth";
        /** The session cookie's full name, when the old app overrode it. */
        private String cookieName;
        /** The {@code domain} the Better Auth cookies were set on, so they clear. */
        private String betterAuthCookieDomain;
        /** Mark the Atlas cookies {@code Secure} (false only for plain-http local development). */
        private boolean secure = true;
        /** Where a user who still owes a second factor goes ({@code __atlas_attempt} / {@code __atlas_status} added). */
        private String signInUrl;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getCookiePrefix() {
            return cookiePrefix;
        }

        public void setCookiePrefix(String cookiePrefix) {
            this.cookiePrefix = cookiePrefix;
        }

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public String getBetterAuthCookieDomain() {
            return betterAuthCookieDomain;
        }

        public void setBetterAuthCookieDomain(String betterAuthCookieDomain) {
            this.betterAuthCookieDomain = betterAuthCookieDomain;
        }

        public boolean isSecure() {
            return secure;
        }

        public void setSecure(boolean secure) {
            this.secure = secure;
        }

        public String getSignInUrl() {
            return signInUrl;
        }

        public void setSignInUrl(String signInUrl) {
            this.signInUrl = signInUrl;
        }
    }

    /** {@code atlas.legacy-issuers[n].*}: see {@link net.atlasauth.atlas.verify.LegacyIssuer}. */
    public static class LegacyIssuerProperties {
        private String issuer;
        /** End of the window: ISO-8601 instant, offset date-time or date. */
        private String until;
        private String jwksUrl;
        private List<String> audience;
        private List<String> algorithms;

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getUntil() {
            return until;
        }

        public void setUntil(String until) {
            this.until = until;
        }

        public String getJwksUrl() {
            return jwksUrl;
        }

        public void setJwksUrl(String jwksUrl) {
            this.jwksUrl = jwksUrl;
        }

        public List<String> getAudience() {
            return audience;
        }

        public void setAudience(List<String> audience) {
            this.audience = audience;
        }

        public List<String> getAlgorithms() {
            return algorithms;
        }

        public void setAlgorithms(List<String> algorithms) {
            this.algorithms = algorithms;
        }
    }
}
