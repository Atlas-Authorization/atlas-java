package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsListResponseDataItem(
        String object,
        String id,
        String slug,
        String name,
        String description,
        String parentId,
        Map<String, Object> attributes,
        List<String> appRoleKeys,
        Boolean includeInToken,
        Map<String, Object> dynamicFilter,
        Map<String, Object> ssoMatch,
        Long memberCount,
        String source,
        Long createdAt,
        Long updatedAt) {
}
