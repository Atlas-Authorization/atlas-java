package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceConnectorsListResponseDataItem(
        String object,
        String id,
        String name,
        String kind,
        String category,
        Map<String, Object> config,
        Boolean enabled,
        Long lastUsedAt,
        String lastStatus,
        Long createdAt,
        Long updatedAt) {
}
