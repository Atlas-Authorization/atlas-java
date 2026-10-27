package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.McpGatewaysBudgetsCreateBody;
import net.atlasauth.atlas.models.McpGatewaysBudgetsListResponse;
import net.atlasauth.atlas.models.McpGatewaysBudgetsListResponseDataItem;
import net.atlasauth.atlas.models.McpGatewaysBudgetsUsageResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/mcp_gateways} resource. */
public final class McpGatewaysBudgets {

    private final Transport t;

    public McpGatewaysBudgets(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/mcp_gateways/{id}/budgets} - The cross-call budgets of a gateway, newest first (at most 50): ceilings on how many calls, how much cost, or how many calls per window one agent or one session may make. Scope: {@code machines:read}. */
    public McpGatewaysBudgetsListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public McpGatewaysBudgetsListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/mcp_gateways/" + enc(id) + "/budgets", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/mcp_gateways/{id}/budgets} - Add a budget. `metric` is `calls` (a count), `cost` (the sum of each call's cost: `cost_per_call`, or the number found at `cost_arg` in the call arguments, such as an amount) or `rate` (calls per `window_seconds`, required). `limit` is the ceiling. `window_seconds` makes the counter reset in fixed windows; without it a counter never resets (a per-session one ends with the session). `scope` is `agent` (each agent counts separately) or `session` (each session of each agent; the client must echo the `Mcp-Session-Id` from initialize). `agent_id` limits it to one agent and `tool` to one tool. A call is charged against every budget it touches at once, or against none; only a call that is allowed and about to run is charged. Scope: {@code machines:write}. */
    public McpGatewaysBudgetsListResponseDataItem create(String id, McpGatewaysBudgetsCreateBody body) {
        return create(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public McpGatewaysBudgetsListResponseDataItem create(String id, McpGatewaysBudgetsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/mcp_gateways/" + enc(id) + "/budgets", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/mcp_gateways/{id}/budgets/{budgetId}} - Read one budget of an MCP gateway. Scope: {@code machines:read}. */
    public McpGatewaysBudgetsListResponseDataItem get(String id, String budgetId) {
        return t.request("GET", "/v1/mcp_gateways/" + enc(id) + "/budgets/" + enc(budgetId), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/mcp_gateways/{id}/budgets/{budgetId}} - Change a budget. Omitted fields keep their value. Raising the limit takes effect on the next call; counters are kept. Scope: {@code machines:write}. */
    public McpGatewaysBudgetsListResponseDataItem update(String id, String budgetId, McpGatewaysBudgetsCreateBody body) {
        return t.request("PATCH", "/v1/mcp_gateways/" + enc(id) + "/budgets/" + enc(budgetId), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/mcp_gateways/{id}/budgets/{budgetId}} - Delete a budget and stop counting against it. Scope: {@code machines:write}. */
    public JsonNode delete(String id, String budgetId) {
        return t.request("DELETE", "/v1/mcp_gateways/" + enc(id) + "/budgets/" + enc(budgetId), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/mcp_gateways/{id}/budgets/{budgetId}/usage} - What one agent has used of a budget right now, with the room left and when the window resets. A per-session budget also needs the `session_id` (the Mcp-Session-Id of that session). Scope: {@code machines:read}. */
    public McpGatewaysBudgetsUsageResponse usage(String id, String budgetId) {
        return usage(id, budgetId, null, null);
    }

    /** As above, with every optional parameter. */
    public McpGatewaysBudgetsUsageResponse usage(String id, String budgetId, String agentId, String sessionId) {
        Map<String, Object> q = Params.of().set("agent_id", agentId).set("session_id", sessionId).map();
        return t.request("GET", "/v1/mcp_gateways/" + enc(id) + "/budgets/" + enc(budgetId) + "/usage", q, null, null, false, new TypeReference<>() {
        });
    }
}
