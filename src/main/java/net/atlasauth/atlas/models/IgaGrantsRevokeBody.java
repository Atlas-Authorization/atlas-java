package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/grants/{id}/revoke}. */
public final class IgaGrantsRevokeBody {
    public String reason;

    public IgaGrantsRevokeBody() {
    }

    public IgaGrantsRevokeBody reason(String v) { this.reason = v; return this; }
}
