package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/mcp_gateways}. Required: name, upstreamUrl. */
public final class McpGatewaysCreateBody {
    /** Required. */
    public String name;
    /** Required. */
    public String upstreamUrl;
    public String upstreamAuthorization;
    public String defaultPolicy;
    public Boolean enabled;
    /** Authorize every tool call through the ABAC policy engine as well as the tool policy. The principal is the agent (type Agent, with name, tier, owner_user_id, organization_id, scopes, data), the action is the tool name, and the resource is the tool (type McpTool, with gateway_id, gateway_name, tool an */
    public Map<String, Object> abac;
    /** Tool name to &#123; allow?, scopes?, conditions?: [&#123; arg, op: eq|neq|lte|gte|in|not_in|present, value &#125;], approval?, mask?: &#123; fields?, presets?: email|card|ssn|phone &#125; &#125;. */
    public Map<String, Object> tools;

    public McpGatewaysCreateBody() {
    }

    public McpGatewaysCreateBody name(String v) { this.name = v; return this; }
    public McpGatewaysCreateBody upstreamUrl(String v) { this.upstreamUrl = v; return this; }
    public McpGatewaysCreateBody upstreamAuthorization(String v) { this.upstreamAuthorization = v; return this; }
    public McpGatewaysCreateBody defaultPolicy(String v) { this.defaultPolicy = v; return this; }
    public McpGatewaysCreateBody enabled(Boolean v) { this.enabled = v; return this; }
    public McpGatewaysCreateBody abac(Map<String, Object> v) { this.abac = v; return this; }
    public McpGatewaysCreateBody tools(Map<String, Object> v) { this.tools = v; return this; }
}
