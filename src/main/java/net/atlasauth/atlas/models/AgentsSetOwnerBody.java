package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/agents/{id}/owner}. Required: userId. */
public final class AgentsSetOwnerBody {
    /** Required. */
    public String userId;

    public AgentsSetOwnerBody() {
    }

    public AgentsSetOwnerBody userId(String v) { this.userId = v; return this; }
}
