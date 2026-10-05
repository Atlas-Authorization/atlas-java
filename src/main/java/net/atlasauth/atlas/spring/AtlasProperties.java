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
}
