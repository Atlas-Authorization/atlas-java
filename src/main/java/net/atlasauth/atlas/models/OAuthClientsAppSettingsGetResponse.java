package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/oauth_clients/{id}/app_settings}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsAppSettingsGetResponse(
        String object,
        String clientId,
        List<String> allowedSignInMethods,
        OAuthClientsAppSettingsGetResponseBranding branding,
        List<String> claimsAllowlist,
        List<String> writableTraits,
        OAuthClientsAppSettingsGetResponseRisk risk) {
}
