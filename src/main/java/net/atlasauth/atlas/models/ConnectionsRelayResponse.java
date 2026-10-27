package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/{id}/relay}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsRelayResponse(
        String object,
        String connectionId,
        Long status,
        Map<String, Object> headers,
        String body,
        Boolean truncated) {
}
