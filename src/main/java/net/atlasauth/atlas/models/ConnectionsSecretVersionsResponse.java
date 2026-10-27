package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/connections/{id}/credentials/versions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsSecretVersionsResponse(
        String object,
        List<ConnectionsSecretVersionsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor,
        String connectionId,
        Long currentVersion,
        Long previousValidUntil) {
}
