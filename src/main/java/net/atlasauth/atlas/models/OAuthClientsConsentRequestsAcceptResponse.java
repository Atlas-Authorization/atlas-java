package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code PUT /v1/oauth_consent_requests/accept}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsConsentRequestsAcceptResponse(
        String object,
        String redirectTo) {
}
