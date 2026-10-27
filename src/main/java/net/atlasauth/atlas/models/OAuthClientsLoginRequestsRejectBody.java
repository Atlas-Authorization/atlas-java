package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/oauth_login_requests/reject}. */
public final class OAuthClientsLoginRequestsRejectBody {
    /** The OAuth error returned to the application. Default access_denied. */
    public String error;
    public String errorDescription;

    public OAuthClientsLoginRequestsRejectBody() {
    }

    public OAuthClientsLoginRequestsRejectBody error(String v) { this.error = v; return this; }
    public OAuthClientsLoginRequestsRejectBody errorDescription(String v) { this.errorDescription = v; return this; }
}
