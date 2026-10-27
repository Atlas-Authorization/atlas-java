package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/mcp_gateways/{id}/budgets}. */
public final class McpGatewaysBudgetsCreateBody {
    public String name;
    public String scope;
    public String metric;
    /** The ceiling, in calls or cost units. */
    public Double limit;
    /** Fixed window length in seconds; null never resets. Required for rate. */
    public Long windowSeconds;
    public String agentId;
    public String tool;
    /** cost: what one call costs when cost_arg is not set. Default 1. */
    public Double costPerCall;
    /** cost: dot path to a number in the call arguments that is the cost of the call. */
    public String costArg;
    public Boolean enabled;

    public McpGatewaysBudgetsCreateBody() {
    }

    public McpGatewaysBudgetsCreateBody name(String v) { this.name = v; return this; }
    public McpGatewaysBudgetsCreateBody scope(String v) { this.scope = v; return this; }
    public McpGatewaysBudgetsCreateBody metric(String v) { this.metric = v; return this; }
    public McpGatewaysBudgetsCreateBody limit(Double v) { this.limit = v; return this; }
    public McpGatewaysBudgetsCreateBody windowSeconds(Long v) { this.windowSeconds = v; return this; }
    public McpGatewaysBudgetsCreateBody agentId(String v) { this.agentId = v; return this; }
    public McpGatewaysBudgetsCreateBody tool(String v) { this.tool = v; return this; }
    public McpGatewaysBudgetsCreateBody costPerCall(Double v) { this.costPerCall = v; return this; }
    public McpGatewaysBudgetsCreateBody costArg(String v) { this.costArg = v; return this; }
    public McpGatewaysBudgetsCreateBody enabled(Boolean v) { this.enabled = v; return this; }
}
