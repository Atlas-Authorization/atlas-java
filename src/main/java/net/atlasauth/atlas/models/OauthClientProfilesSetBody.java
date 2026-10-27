package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/oauth_client_profiles/{name}}. */
public final class OauthClientProfilesSetBody {
    public String description;
    public Boolean requirePkce;
    /** Authorization requests must be pushed (RFC 9126). */
    public Boolean requirePar;
    /** The token request must prove DPoP, mutual TLS, or either (any). */
    public String senderConstraint;
    /** Authorization requests must be a signed request object (RFC 9101), pushed or direct. */
    public Boolean requireSignedRequest;
    /** Allowed client authentication methods; null = any. */
    public List<String> tokenEndpointAuthMethods;
    /** JWS algorithms accepted for request objects and client assertions; null = any asymmetric. */
    public List<String> signingAlgs;
    public Boolean httpsRedirectsOnly;
    /** Claim names a `claims` request may ask for; null = any. */
    public List<String> allowedClaims;

    public OauthClientProfilesSetBody() {
    }

    public OauthClientProfilesSetBody description(String v) { this.description = v; return this; }
    public OauthClientProfilesSetBody requirePkce(Boolean v) { this.requirePkce = v; return this; }
    public OauthClientProfilesSetBody requirePar(Boolean v) { this.requirePar = v; return this; }
    public OauthClientProfilesSetBody senderConstraint(String v) { this.senderConstraint = v; return this; }
    public OauthClientProfilesSetBody requireSignedRequest(Boolean v) { this.requireSignedRequest = v; return this; }
    public OauthClientProfilesSetBody tokenEndpointAuthMethods(List<String> v) { this.tokenEndpointAuthMethods = v; return this; }
    public OauthClientProfilesSetBody signingAlgs(List<String> v) { this.signingAlgs = v; return this; }
    public OauthClientProfilesSetBody httpsRedirectsOnly(Boolean v) { this.httpsRedirectsOnly = v; return this; }
    public OauthClientProfilesSetBody allowedClaims(List<String> v) { this.allowedClaims = v; return this; }
}
