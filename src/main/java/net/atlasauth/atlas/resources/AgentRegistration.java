package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AgentRegistrationGetResponse;
import net.atlasauth.atlas.models.AgentRegistrationReplaceBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/agent_registration} resource. */
public final class AgentRegistration {

    private final Transport t;

    public AgentRegistration(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/agent_registration} - Read the agent registration settings (enabled, allowed identity kinds, untrusted and trusted scopes, claim code lifetime). Scope: {@code machines:read}. */
    public AgentRegistrationGetResponse get() {
        return t.request("GET", "/v1/agent_registration", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/agent_registration} - Update the agent registration settings. Partial: only the fields sent change. Scope: {@code machines:write}. */
    public JsonNode replace(AgentRegistrationReplaceBody body) {
        return t.request("PUT", "/v1/agent_registration", null, body, null, false, new TypeReference<JsonNode>() {
        });
    }
}
