package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/export}. */
public final class ConnectionsExportConnectionsBody {
    public Object ownerType;
    public String ownerId;
    public String provider;
    /** The provider environment the credential belongs to. sandbox selects the provider's test hosts (Salesforce test.salesforce.com, PayPal and QuickBooks sandboxes, DocuSign demo...). */
    public Object environment;
    public Object status;
    public Long limit;
    public String startingAfter;
    public Boolean includeSecrets;
    /** Required with include_secrets: a PUBLIC JWK. A key with private parameters is refused. */
    public Map<String, Object> recipientPublicKey;

    public ConnectionsExportConnectionsBody() {
    }

    public ConnectionsExportConnectionsBody ownerType(Object v) { this.ownerType = v; return this; }
    public ConnectionsExportConnectionsBody ownerId(String v) { this.ownerId = v; return this; }
    public ConnectionsExportConnectionsBody provider(String v) { this.provider = v; return this; }
    public ConnectionsExportConnectionsBody environment(Object v) { this.environment = v; return this; }
    public ConnectionsExportConnectionsBody status(Object v) { this.status = v; return this; }
    public ConnectionsExportConnectionsBody limit(Long v) { this.limit = v; return this; }
    public ConnectionsExportConnectionsBody startingAfter(String v) { this.startingAfter = v; return this; }
    public ConnectionsExportConnectionsBody includeSecrets(Boolean v) { this.includeSecrets = v; return this; }
    public ConnectionsExportConnectionsBody recipientPublicKey(Map<String, Object> v) { this.recipientPublicKey = v; return this; }
}
