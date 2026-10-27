package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/access_policies}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessPoliciesListResponse(
        String object,
        List<AccessPoliciesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
