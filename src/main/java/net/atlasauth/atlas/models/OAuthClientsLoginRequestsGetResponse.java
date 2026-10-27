package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/oauth_login_requests}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsLoginRequestsGetResponse(
        String object,
        OAuthClientsLoginRequestsGetResponseClient client,
        List<String> requestedScope,
        String audience,
        String page,
        String sessionSubject,
        String uiLocales,
        String loginHint,
        Long expiresAt) {
}
