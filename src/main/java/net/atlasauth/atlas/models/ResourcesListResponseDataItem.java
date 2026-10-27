package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResourcesListResponseDataItem(
        String object,
        String id,
        String type,
        String externalId,
        String name,
        String parentId,
        Map<String, Object> metadata,
        Long createdAt,
        Long updatedAt) {
}
