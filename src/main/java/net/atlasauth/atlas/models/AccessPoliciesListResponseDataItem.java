package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessPoliciesListResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        Boolean enabled,
        Long priority,
        List<String> stages,
        Map<String, Object> conditions,
        List<Map<String, Object>> grants,
        Long createdAt,
        Long updatedAt) {
}
