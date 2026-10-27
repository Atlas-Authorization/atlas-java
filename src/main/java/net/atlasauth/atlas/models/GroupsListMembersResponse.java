package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/groups/{id}/members}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsListMembersResponse(
        String object,
        List<GroupsListMembersResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
