package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code PUT /v1/oauth_clients/{id}/delegated_ui}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsSetDelegatedUiResponse(
        String object,
        String clientId,
        String loginUrl,
        String consentUrl) {
}
