package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AgentAccessRequestsListResponse;
import net.atlasauth.atlas.models.AgentsClaimBody;
import net.atlasauth.atlas.models.AgentsRevokeAccessBody;
import net.atlasauth.atlas.models.AgentsSetOwnerBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/agents} resource. */
public final class Agents {

    private final Transport t;

    public Agents(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/agents/{id}/claim} - Backend-assisted claim: bind an agent to a user you already know and promote it to trusted. Audited as agent.claimed. Scope: {@code machines:write}. */
    public JsonNode claim(String id, AgentsClaimBody body) {
        return claim(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode claim(String id, AgentsClaimBody body, String idempotencyKey) {
        return t.request("POST", "/v1/agents/" + enc(id) + "/claim", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/agents} - The agent registry: every registered agent with its tier, owner and status. Filter by owner_user_id or tier; page with limit and starting_after. Scope: {@code machines:read}. */
    public AgentAccessRequestsListResponse list() {
        return list(null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AgentAccessRequestsListResponse list(String ownerUserId, String tier, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("owner_user_id", ownerUserId).set("tier", tier).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/agents", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/agents/{id}} - One agent with its currently valid access grants. Scope: {@code machines:read}. */
    public JsonNode get(String id) {
        return t.request("GET", "/v1/agents/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code PUT /v1/agents/{id}/owner} - Set the owning user of an agent, or null to remove the owner (which also drops the agent back to untrusted). Scope: {@code machines:write}. */
    public JsonNode setOwner(String id, AgentsSetOwnerBody body) {
        return t.request("PUT", "/v1/agents/" + enc(id) + "/owner", null, body, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/agents/{id}/revoke_access} - Revoke every grant and cancel every pending request of an agent. With disable: true the agent itself is revoked and can no longer get tokens. Emits agent.access_revoked. Scope: {@code machines:write}. */
    public JsonNode revokeAccess(String id, AgentsRevokeAccessBody body) {
        return revokeAccess(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode revokeAccess(String id, AgentsRevokeAccessBody body, String idempotencyKey) {
        return t.request("POST", "/v1/agents/" + enc(id) + "/revoke_access", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }
}
