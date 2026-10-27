package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/oauth_clients/{id}/delegated_ui}. */
public final class OAuthClientsSetDelegatedUiBody {
    /** https URL of your sign-in page; null clears it. Omit to leave unchanged. */
    public String loginUrl;
    /** https URL of your consent page; null clears it. Omit to leave unchanged. */
    public String consentUrl;

    public OAuthClientsSetDelegatedUiBody() {
    }

    public OAuthClientsSetDelegatedUiBody loginUrl(String v) { this.loginUrl = v; return this; }
    public OAuthClientsSetDelegatedUiBody consentUrl(String v) { this.consentUrl = v; return this; }
}
