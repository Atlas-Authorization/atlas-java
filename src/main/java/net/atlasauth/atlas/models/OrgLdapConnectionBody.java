package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrgLdapConnectionBody {
    /** The owning organization (required on create). */
    public String organizationId;
    public String status;
    /** ldap:// or ldaps:// directory URL. */
    public String url;
    /** Service-account DN Atlas binds as to search. */
    public String bindDn;
    /** Subtree searched under. */
    public String baseDn;
    /** Search filter with a {{username}} placeholder. */
    public String userFilter;
    public Boolean startTls;
    /** Directory attribute → Atlas field mapping. `email` is required. */
    public OrgLdapConnectionBodyAttributes attributes;
    /** Service-account password. Write-only; never returned. */
    public String bindPassword;
    public String defaultRoleId;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OrgLdapConnectionBody() {
    }

    public OrgLdapConnectionBody organizationId(String v) { this.organizationId = v; return this; }
    public OrgLdapConnectionBody status(String v) { this.status = v; return this; }
    public OrgLdapConnectionBody url(String v) { this.url = v; return this; }
    public OrgLdapConnectionBody bindDn(String v) { this.bindDn = v; return this; }
    public OrgLdapConnectionBody baseDn(String v) { this.baseDn = v; return this; }
    public OrgLdapConnectionBody userFilter(String v) { this.userFilter = v; return this; }
    public OrgLdapConnectionBody startTls(Boolean v) { this.startTls = v; return this; }
    public OrgLdapConnectionBody attributes(OrgLdapConnectionBodyAttributes v) { this.attributes = v; return this; }
    public OrgLdapConnectionBody bindPassword(String v) { this.bindPassword = v; return this; }
    public OrgLdapConnectionBody defaultRoleId(String v) { this.defaultRoleId = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OrgLdapConnectionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
