package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/mcp_gateways/{id}}. */
public final class McpGatewaysUpdateBody {
    /** Authorize every tool call through the ABAC policy engine as well as the tool policy. The principal is the agent (type Agent, with name, tier, owner_user_id, organization_id, scopes, data), the action is the tool name, and the resource is the tool (type McpTool, with gateway_id, gateway_name, tool an */
    public Map<String, Object> abac;

    public McpGatewaysUpdateBody() {
    }

    public McpGatewaysUpdateBody abac(Map<String, Object> v) { this.abac = v; return this; }
}
