package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The result of forcing one user's sync now. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScimProvisioningSyncResult(
        String object,
        String id,
        String userId,
        String action,
        boolean ok,
        Integer status,
        String remoteId,
        String error) {
}
