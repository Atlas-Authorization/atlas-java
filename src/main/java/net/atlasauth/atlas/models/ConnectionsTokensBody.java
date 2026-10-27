package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/tokens/batch}. Required: ids. */
public final class ConnectionsTokensBody {
    /** Connection ids (duplicates are read once). Required. */
    public List<String> ids;
    public Long minTtlSeconds;

    public ConnectionsTokensBody() {
    }

    public ConnectionsTokensBody ids(List<String> v) { this.ids = v; return this; }
    public ConnectionsTokensBody minTtlSeconds(Long v) { this.minTtlSeconds = v; return this; }
}
