package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/vault/objects/{id}/versions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VaultVersionsResponse(
        String object,
        List<VaultVersionsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
