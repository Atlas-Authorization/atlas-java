package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/org_ldap_connections/{id}/sync}. */
public final class OrgLdapConnectionsSetSyncBody {
    public Boolean enabled;
    @JsonProperty("intervalMinutes")
    public Double intervalMinutes;
    @JsonProperty("pageSize")
    public Double pageSize;
    @JsonProperty("idAttribute")
    public String idAttribute;
    @JsonProperty("userFilter")
    public String userFilter;
    @JsonProperty("groupBaseDN")
    public String groupBaseDN;
    @JsonProperty("groupFilter")
    public String groupFilter;
    @JsonProperty("groupMemberAttribute")
    public String groupMemberAttribute;
    @JsonProperty("groupNameAttribute")
    public String groupNameAttribute;
    @JsonProperty("syncGroups")
    public Boolean syncGroups;
    @JsonProperty("roleMappings")
    public List<Map<String, Object>> roleMappings;
    @JsonProperty("disableOnRemoval")
    public Boolean disableOnRemoval;
    @JsonProperty("deltaAttribute")
    public String deltaAttribute;

    public OrgLdapConnectionsSetSyncBody() {
    }

    public OrgLdapConnectionsSetSyncBody enabled(Boolean v) { this.enabled = v; return this; }
    public OrgLdapConnectionsSetSyncBody intervalMinutes(Double v) { this.intervalMinutes = v; return this; }
    public OrgLdapConnectionsSetSyncBody pageSize(Double v) { this.pageSize = v; return this; }
    public OrgLdapConnectionsSetSyncBody idAttribute(String v) { this.idAttribute = v; return this; }
    public OrgLdapConnectionsSetSyncBody userFilter(String v) { this.userFilter = v; return this; }
    public OrgLdapConnectionsSetSyncBody groupBaseDN(String v) { this.groupBaseDN = v; return this; }
    public OrgLdapConnectionsSetSyncBody groupFilter(String v) { this.groupFilter = v; return this; }
    public OrgLdapConnectionsSetSyncBody groupMemberAttribute(String v) { this.groupMemberAttribute = v; return this; }
    public OrgLdapConnectionsSetSyncBody groupNameAttribute(String v) { this.groupNameAttribute = v; return this; }
    public OrgLdapConnectionsSetSyncBody syncGroups(Boolean v) { this.syncGroups = v; return this; }
    public OrgLdapConnectionsSetSyncBody roleMappings(List<Map<String, Object>> v) { this.roleMappings = v; return this; }
    public OrgLdapConnectionsSetSyncBody disableOnRemoval(Boolean v) { this.disableOnRemoval = v; return this; }
    public OrgLdapConnectionsSetSyncBody deltaAttribute(String v) { this.deltaAttribute = v; return this; }
}
