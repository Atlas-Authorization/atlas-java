package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/group_rules/apply}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsRulesApplyAllResponse(
        String object,
        List<GroupsRulesApplyAllResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
