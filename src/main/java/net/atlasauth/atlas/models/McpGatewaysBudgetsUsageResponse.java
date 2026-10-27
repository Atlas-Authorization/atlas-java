package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/mcp_gateways/{id}/budgets/{budgetId}/usage}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record McpGatewaysBudgetsUsageResponse(
        String object,
        String budgetId,
        String agentId,
        Double used,
        Double limit,
        Double remaining,
        Long resetsAt) {
}
