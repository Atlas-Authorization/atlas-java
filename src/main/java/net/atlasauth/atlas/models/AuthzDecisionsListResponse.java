package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/authz_decisions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthzDecisionsListResponse(
        String object,
        List<AuthzDecisionsListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
