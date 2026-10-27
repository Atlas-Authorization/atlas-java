package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/connections/{id}/token}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsTokenResponse(
        String object,
        String connectionId,
        String accessToken,
        Long expiresAt,
        List<String> scopes,
        Map<String, Object> metadata,
        Map<String, Object> tokenResponse,
        Map<String, Object> credentials,
        String externalId,
        Object environment,
        Map<String, Object> hostParams,
        Boolean refreshed,
        String subject) {
}
