package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/ssf/streams/{id}/status}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsfStreamsGetStatusResponse(
        String object) {
}
