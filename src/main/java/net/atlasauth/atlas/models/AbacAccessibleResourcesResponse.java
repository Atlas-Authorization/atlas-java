package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/abac/accessible_resources}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacAccessibleResourcesResponse(
        String object,
        List<AbacAccessibleResourcesResponseDataItem> data,
        Boolean hasMore,
        String nextCursor,
        Long total,
        Long evaluated,
        String method) {
}
