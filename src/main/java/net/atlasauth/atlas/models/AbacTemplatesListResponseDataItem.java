package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacTemplatesListResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        String source,
        Long version,
        List<String> slots,
        String fgaStoreId,
        Long linkCount,
        Long linksUpdated,
        Long createdAt,
        Long updatedAt) {
}
