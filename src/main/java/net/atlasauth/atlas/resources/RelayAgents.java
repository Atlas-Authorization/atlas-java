package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.RelayAgentsCreateBody;
import net.atlasauth.atlas.models.RelayAgentsListResponse;
import net.atlasauth.atlas.models.RelayAgentsListResponseDataItem;
import net.atlasauth.atlas.models.RelayAgentsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/relay_agents} resource. */
public final class RelayAgents {

    private final Transport t;

    public RelayAgents(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/relay_agents} - List the on-prem relay agents with their allowed destinations and whether each is online. Secrets are never returned. Scope: {@code relay_agents:read}. */
    public RelayAgentsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public RelayAgentsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/relay_agents", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/relay_agents} - Register an outbound-only relay agent. The response carries the agent token once; it cannot be read back. An agent can reach only the destinations in allowed_destinations (host, host:port, *.domain). Scope: {@code relay_agents:write}. */
    public RelayAgentsListResponseDataItem create(RelayAgentsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public RelayAgentsListResponseDataItem create(RelayAgentsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/relay_agents", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/relay_agents/{id}} - Read one relay agent, including when it last polled. Scope: {@code relay_agents:read}. */
    public RelayAgentsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/relay_agents/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/relay_agents/{id}} - Rename an agent, change its allowed destinations or pinned client-certificate thumbprints, or revoke it (status: revoked). Scope: {@code relay_agents:write}. */
    public RelayAgentsListResponseDataItem update(String id, RelayAgentsUpdateBody body) {
        return t.request("PATCH", "/v1/relay_agents/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/relay_agents/{id}} - Delete a relay agent and its queued jobs. Scope: {@code relay_agents:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/relay_agents/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/relay_agents/{id}/rotate_secret} - Issue a new agent token (returned once). The old token stops working immediately. Scope: {@code relay_agents:write}. */
    public RelayAgentsListResponseDataItem rotateSecret(String id) {
        return rotateSecret(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public RelayAgentsListResponseDataItem rotateSecret(String id, String idempotencyKey) {
        return t.request("POST", "/v1/relay_agents/" + enc(id) + "/rotate_secret", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
