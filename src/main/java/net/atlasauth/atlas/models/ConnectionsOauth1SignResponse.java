package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/{id}/oauth1/sign}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsOauth1SignResponse(
        String object,
        String connectionId,
        String authorization,
        String method,
        String url) {
}
