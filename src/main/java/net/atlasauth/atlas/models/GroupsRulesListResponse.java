package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/group_rules}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsRulesListResponse(
        String object,
        List<GroupsRulesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
