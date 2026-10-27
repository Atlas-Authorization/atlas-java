package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/connections/{id}/webhook_endpoints}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsWebhooksListEndpointsResponse(
        String object,
        List<ConnectionsWebhooksCreateEndpointResponse> data,
        Boolean hasMore,
        String nextCursor) {
}
