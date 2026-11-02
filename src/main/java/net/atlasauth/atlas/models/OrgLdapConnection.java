package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgLdapConnection(
        String object,
        String id,
        String organizationId,
        String status,
        String url,
        String bindDn,
        String baseDn,
        String userFilter,
        Boolean startTls,
        OrgLdapConnectionBodyAttributes attributes,
        Boolean hasBindPassword,
        String defaultRoleId,
        Long createdAt,
        Long updatedAt,
        /** Directory group DN to organization role; the first group the person is in picks their role. */
        List<GroupRoleMapping> groupRoleMappings) {

    /** The 1.19 shape, without groupRoleMappings. */
    public OrgLdapConnection(String object, String id, String organizationId, String status, String url, String bindDn, String baseDn, String userFilter, Boolean startTls, OrgLdapConnectionBodyAttributes attributes, Boolean hasBindPassword, String defaultRoleId, Long createdAt, Long updatedAt) {
        this(object, id, organizationId, status, url, bindDn, baseDn, userFilter, startTls, attributes, hasBindPassword, defaultRoleId, createdAt, updatedAt, null);
    }

    /** A directory group DN and the organization role key it maps to. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GroupRoleMapping(String group, String role) {
    }
}
