package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/oauth_login_requests/accept}. Required: subject. */
public final class OAuthClientsLoginRequestsAcceptBody {
    /** The Atlas user id the visitor authenticated as. Required. */
    public String subject;
    /** Keep the Atlas session across browser restarts. */
    public Boolean remember;
    /** Authentication methods used, recorded on the session and echoed in the id_token. */
    public List<String> amr;
    /** Authentication context class reached. */
    public String acr;

    public OAuthClientsLoginRequestsAcceptBody() {
    }

    public OAuthClientsLoginRequestsAcceptBody subject(String v) { this.subject = v; return this; }
    public OAuthClientsLoginRequestsAcceptBody remember(Boolean v) { this.remember = v; return this; }
    public OAuthClientsLoginRequestsAcceptBody amr(List<String> v) { this.amr = v; return this; }
    public OAuthClientsLoginRequestsAcceptBody acr(String v) { this.acr = v; return this; }
}
