package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/import}. Required: connections. */
public final class ConnectionsImportConnectionsBody {
    /** Required. */
    public List<Map<String, Object>> connections;
    /** Spend each refresh token against the provider and store what it issues. */
    public Boolean validate;
    public Boolean dryRun;

    public ConnectionsImportConnectionsBody() {
    }

    public ConnectionsImportConnectionsBody connections(List<Map<String, Object>> v) { this.connections = v; return this; }
    public ConnectionsImportConnectionsBody validate(Boolean v) { this.validate = v; return this; }
    public ConnectionsImportConnectionsBody dryRun(Boolean v) { this.dryRun = v; return this; }
}
