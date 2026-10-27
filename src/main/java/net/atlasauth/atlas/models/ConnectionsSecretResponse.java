package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/connections/{id}/secret}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsSecretResponse(
        String object,
        String connectionId,
        Object kind,
        Long version,
        Map<String, Object> secret,
        Map<String, Object> config,
        Long expiresAt) {
}
