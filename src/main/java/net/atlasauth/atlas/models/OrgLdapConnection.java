package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
        Long updatedAt) {
}
