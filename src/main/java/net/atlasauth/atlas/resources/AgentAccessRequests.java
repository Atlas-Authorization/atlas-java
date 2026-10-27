package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AgentAccessRequestsApproveBody;
import net.atlasauth.atlas.models.AgentAccessRequestsDenyBody;
import net.atlasauth.atlas.models.AgentAccessRequestsListResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/agent_access_requests} resource. */
public final class AgentAccessRequests {

    private final Transport t;

    public AgentAccessRequests(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/agent_access_requests/{id}/approve} - Approve a pending request, optionally for a limited time (expires_in_seconds). 409 when it is no longer pending. Emits agent.access_decided. Scope: {@code machines:write}. */
    public JsonNode approve(String id, AgentAccessRequestsApproveBody body) {
        return approve(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode approve(String id, AgentAccessRequestsApproveBody body, String idempotencyKey) {
        return t.request("POST", "/v1/agent_access_requests/" + enc(id) + "/approve", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/agent_access_requests/{id}/deny} - Deny a pending request. 409 when it is no longer pending. Emits agent.access_decided. Scope: {@code machines:write}. */
    public JsonNode deny(String id, AgentAccessRequestsDenyBody body) {
        return deny(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode deny(String id, AgentAccessRequestsDenyBody body, String idempotencyKey) {
        return t.request("POST", "/v1/agent_access_requests/" + enc(id) + "/deny", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/agent_access_requests} - List agent access requests, newest first. Filter by status (pending, approved, denied, revoked) or agent_id. Scope: {@code machines:read}. */
    public AgentAccessRequestsListResponse list() {
        return list(null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AgentAccessRequestsListResponse list(String status, String agentId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("status", status).set("agent_id", agentId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/agent_access_requests", q, null, null, false, new TypeReference<>() {
        });
    }
}
