package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsRulesListResponseDataItem(
        String object,
        String id,
        String name,
        Boolean enabled,
        Long priority,
        Map<String, Object> condition,
        List<Map<String, Object>> actions,
        String createdBy,
        Long createdAt,
        Long updatedAt) {
}
