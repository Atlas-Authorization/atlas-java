package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/abac_templates}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacTemplatesListResponse(
        String object,
        List<AbacTemplatesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
