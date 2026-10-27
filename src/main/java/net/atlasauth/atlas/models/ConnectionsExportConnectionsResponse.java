package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/export}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsExportConnectionsResponse(
        String object,
        Boolean sealed,
        Long count,
        Boolean hasMore,
        String nextCursor,
        List<Map<String, Object>> connections,
        String recipientKeyId,
        String bundle) {
}
