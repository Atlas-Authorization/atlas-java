package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The result of forcing one org's (group) sync now. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScimProvisioningGroupSyncResult(
        String object,
        String id,
        String organizationId,
        String action,
        boolean ok,
        Integer status,
        String remoteId,
        int memberCount,
        String error) {
}
