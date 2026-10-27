package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/oauth_consent_requests/accept}. */
public final class OAuthClientsConsentRequestsAcceptBody {
    /** The scopes to grant: a subset of requested_scope that includes openid. Default: all requested. */
    public List<String> grantScope;
    /** Record the consent so the user is not asked again. Default true. */
    public Boolean remember;

    public OAuthClientsConsentRequestsAcceptBody() {
    }

    public OAuthClientsConsentRequestsAcceptBody grantScope(List<String> v) { this.grantScope = v; return this; }
    public OAuthClientsConsentRequestsAcceptBody remember(Boolean v) { this.remember = v; return this; }
}
