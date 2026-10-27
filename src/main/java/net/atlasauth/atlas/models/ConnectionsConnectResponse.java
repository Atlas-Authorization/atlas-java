package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/{id}/connect}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsConnectResponse(
        String object,
        String connectionId,
        String authorizationUrl,
        String attemptId) {
}
