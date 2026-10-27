package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.McpGatewaysCreateBody;
import net.atlasauth.atlas.models.McpGatewaysUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/mcp_gateways} resource. */
public final class McpGateways {

    private final Transport t;

    public McpGateways(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/mcp_gateways} - List the MCP tool gateways of the instance. Scope: {@code machines:read}. */
    public JsonNode list() {
        return t.request("GET", "/v1/mcp_gateways", null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/mcp_gateways} - Create an MCP gateway: one URL (`url` in the response) that fronts your MCP server. `tools` maps a tool name to its policy (allow, scopes the agent token must hold, argument conditions, approval, response masking); `default_policy` (deny by default) decides tools not listed. upstream_authorization is write-only and sent to the upstream as its Authorization header. Scope: {@code machines:write}. */
    public JsonNode create(McpGatewaysCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode create(McpGatewaysCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/mcp_gateways", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/mcp_gateways/{id}} - Read one MCP gateway. Scope: {@code machines:read}. */
    public JsonNode get(String id) {
        return t.request("GET", "/v1/mcp_gateways/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code PATCH /v1/mcp_gateways/{id}} - Update an MCP gateway. Omitted fields keep their value; `tools` is replaced as a whole. `abac` turns on authorization of every tool call through the ABAC policy engine. Scope: {@code machines:write}. */
    public JsonNode update(String id, McpGatewaysUpdateBody body) {
        return t.request("PATCH", "/v1/mcp_gateways/" + enc(id), null, body, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code DELETE /v1/mcp_gateways/{id}} - Delete an MCP gateway. Scope: {@code machines:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/mcp_gateways/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** The {@code budgets} sub-resource. */
    public McpGatewaysBudgets budgets() {
        return new McpGatewaysBudgets(t);
    }
}
