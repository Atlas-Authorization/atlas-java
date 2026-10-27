package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/{id}/token/refresh}. */
public final class ConnectionsRefreshTokenBody {
    /** Why you are refreshing; recorded in the audit log. */
    public Object reason;
    /** SHA-256 (hex) of the access token the provider refused. */
    public String staleTokenHash;

    public ConnectionsRefreshTokenBody() {
    }

    public ConnectionsRefreshTokenBody reason(Object v) { this.reason = v; return this; }
    public ConnectionsRefreshTokenBody staleTokenHash(String v) { this.staleTokenHash = v; return this; }
}
