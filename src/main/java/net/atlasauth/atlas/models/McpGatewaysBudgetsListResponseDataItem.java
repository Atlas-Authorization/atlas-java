package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record McpGatewaysBudgetsListResponseDataItem(
        String object,
        String id,
        String gatewayId,
        String name,
        String scope,
        String agentId,
        String tool,
        String metric,
        Double limit,
        Long windowSeconds,
        Double costPerCall,
        String costArg,
        Boolean enabled,
        Long createdAt) {
}
