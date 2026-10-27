package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsListMembersResponseDataItem(
        String object,
        String groupId,
        String userId,
        String source,
        String sourceRef,
        Long createdAt) {
}
