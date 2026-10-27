package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/flows/{id}/versions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneysVersionsResponse(
        String object,
        List<JourneysVersionsResponseDataItem> data) {
}
