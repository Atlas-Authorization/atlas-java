package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResourcesTypesListResponseDataItem(
        String object,
        String key,
        String name,
        String description,
        String parentType,
        Boolean inherit,
        List<Map<String, Object>> roles,
        Long createdAt,
        Long updatedAt) {
}
