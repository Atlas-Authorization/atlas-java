package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/agents/{id}/claim}. Required: userId. */
public final class AgentsClaimBody {
    /** Required. */
    public String userId;

    public AgentsClaimBody() {
    }

    public AgentsClaimBody userId(String v) { this.userId = v; return this; }
}
