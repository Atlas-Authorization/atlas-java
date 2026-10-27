package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/org_ldap_connections/{id}/relay}. */
public final class OrgLdapConnectionsSetRelayBody {
    public String agentId;

    public OrgLdapConnectionsSetRelayBody() {
    }

    public OrgLdapConnectionsSetRelayBody agentId(String v) { this.agentId = v; return this; }
}
