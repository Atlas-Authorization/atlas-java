package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/agent_access_requests/{id}/deny}. */
public final class AgentAccessRequestsDenyBody {
    public String decidedBy;

    public AgentAccessRequestsDenyBody() {
    }

    public AgentAccessRequestsDenyBody decidedBy(String v) { this.decidedBy = v; return this; }
}
