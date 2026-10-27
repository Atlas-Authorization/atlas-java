package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/oauth_consent_requests}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsConsentRequestsGetResponse(
        String object,
        String subject,
        OAuthClientsLoginRequestsGetResponseClient client,
        List<String> requestedScope,
        String audience,
        List<Map<String, Object>> authorizationDetails,
        String organizationId,
        String redirectUri,
        Long expiresAt) {
}
