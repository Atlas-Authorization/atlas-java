package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/abac_policies/{id}/versions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacVersionsResponse(
        String object,
        List<AbacVersionsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
