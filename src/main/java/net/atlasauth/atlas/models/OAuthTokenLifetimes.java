package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Token lifetimes in seconds. A null field inherits the API's, then the instance's, then
 * Atlas's default. Bounds: access / id token 60–86400; refresh token 300–31536000 (idle ≤
 * absolute); authorization code 10–600; device code 60–1800.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OAuthTokenLifetimes {
    public Long accessTokenTtlSeconds;
    public Long idTokenTtlSeconds;
    public Long refreshTokenAbsoluteTtlSeconds;
    public Long refreshTokenIdleTtlSeconds;
    public Long authorizationCodeTtlSeconds;
    public Long deviceCodeTtlSeconds;

    public OAuthTokenLifetimes() {
    }

    public OAuthTokenLifetimes accessTokenTtlSeconds(Long v) { this.accessTokenTtlSeconds = v; return this; }
    public OAuthTokenLifetimes idTokenTtlSeconds(Long v) { this.idTokenTtlSeconds = v; return this; }
    public OAuthTokenLifetimes refreshTokenAbsoluteTtlSeconds(Long v) { this.refreshTokenAbsoluteTtlSeconds = v; return this; }
    public OAuthTokenLifetimes refreshTokenIdleTtlSeconds(Long v) { this.refreshTokenIdleTtlSeconds = v; return this; }
    public OAuthTokenLifetimes authorizationCodeTtlSeconds(Long v) { this.authorizationCodeTtlSeconds = v; return this; }
    public OAuthTokenLifetimes deviceCodeTtlSeconds(Long v) { this.deviceCodeTtlSeconds = v; return this; }
}
