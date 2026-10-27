package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/resource_checks/batch}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResourcesCheckBatchResponse(
        String object,
        List<ResourcesCheckResponse> data,
        Boolean hasMore,
        String nextCursor) {
}
