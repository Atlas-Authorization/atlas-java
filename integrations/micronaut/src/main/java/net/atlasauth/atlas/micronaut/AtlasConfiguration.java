package net.atlasauth.atlas.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.List;

/**
 * <pre>
 * atlas:
 *   issuer: https://auth.example.com
 *   jwks-url: https://auth.example.com/.well-known/jwks.json
 *   authorized-parties: [https://app.example.com]
 * </pre>
 */
@ConfigurationProperties("atlas")
public class AtlasConfiguration {

    private String issuer;
    private String jwksUrl;
    private List<String> authorizedParties;
    private String cookieName = "__session";
    private final AccessToken accessToken = new AccessToken();

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getJwksUrl() {
        return jwksUrl;
    }

    public void setJwksUrl(String jwksUrl) {
        this.jwksUrl = jwksUrl;
    }

    public List<String> getAuthorizedParties() {
        return authorizedParties;
    }

    public void setAuthorizedParties(List<String> authorizedParties) {
        this.authorizedParties = authorizedParties;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    /**
     * OAuth access tokens for your API (off unless {@code audience} is set):
     * <pre>
     * atlas:
     *   access-token:
     *     audience: https://api.example.com
     *     required-scopes: [orders:read]
     * </pre>
     */
    @ConfigurationProperties("access-token")
    public static class AccessToken {
        private String audience;
        private List<String> requiredScopes;

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public List<String> getRequiredScopes() {
            return requiredScopes;
        }

        public void setRequiredScopes(List<String> requiredScopes) {
            this.requiredScopes = requiredScopes;
        }
    }
}
