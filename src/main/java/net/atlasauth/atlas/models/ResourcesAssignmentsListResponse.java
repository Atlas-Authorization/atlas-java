package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/resource_role_assignments}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResourcesAssignmentsListResponse(
        String object,
        List<ResourcesAssignmentsListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
