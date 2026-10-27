package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/fga/stores/{id}/changes}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaReadChangesResponse(
        String object,
        List<FgaReadChangesResponseDataItem> data,
        Boolean hasMore,
        String continuationToken) {
}
