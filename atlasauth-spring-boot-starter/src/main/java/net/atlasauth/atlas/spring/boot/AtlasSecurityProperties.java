package net.atlasauth.atlas.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * {@code atlas.security.*}: how the default security chain treats requests. The verifier itself is configured by
 * {@code atlas.issuer} and {@code atlas.jwks-url}.
 *
 * <pre>
 * atlas:
 *   issuer: https://clerk.example.com
 *   jwks-url: https://api.atlasauth.net/v1/jwks
 *   security:
 *     public-paths: [/, /health, /public/**]
 * </pre>
 */
@ConfigurationProperties("atlas.security")
public class AtlasSecurityProperties {

    /** Register the Atlas filter and the default security chain. Default true. */
    private boolean enabled = true;

    /** Ant patterns the default chain lets through without a session. Everything else needs one. */
    private List<String> publicPaths = List.of("/error");

    /** The cookie carrying the session token when no Authorization header is sent. */
    private String cookieName = "__session";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
