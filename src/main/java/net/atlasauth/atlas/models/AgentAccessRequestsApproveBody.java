package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/agent_access_requests/{id}/approve}. */
public final class AgentAccessRequestsApproveBody {
    public Long expiresInSeconds;
    public String decidedBy;

    public AgentAccessRequestsApproveBody() {
    }

    public AgentAccessRequestsApproveBody expiresInSeconds(Long v) { this.expiresInSeconds = v; return this; }
    public AgentAccessRequestsApproveBody decidedBy(String v) { this.decidedBy = v; return this; }
}
