package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/agents/{id}/revoke_access}. */
public final class AgentsRevokeAccessBody {
    public Boolean disable;

    public AgentsRevokeAccessBody() {
    }

    public AgentsRevokeAccessBody disable(Boolean v) { this.disable = v; return this; }
}
