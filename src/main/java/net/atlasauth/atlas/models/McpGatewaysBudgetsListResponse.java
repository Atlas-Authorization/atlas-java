package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/mcp_gateways/{id}/budgets}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record McpGatewaysBudgetsListResponse(
        String object,
        List<McpGatewaysBudgetsListResponseDataItem> data,
        Boolean hasMore) {
}
