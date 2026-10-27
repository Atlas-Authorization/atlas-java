package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacListResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        Boolean enabled,
        Long currentVersion,
        String fgaStoreId,
        String source,
        List<Map<String, Object>> tests,
        Long createdAt,
        Long updatedAt) {
}
