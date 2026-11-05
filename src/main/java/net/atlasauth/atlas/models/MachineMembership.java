package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A machine as an organization member with a role. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MachineMembership(
        String object,
        String id,
        String organizationId,
        String machineId,
        /** The organization role key. */ String role,
        String roleId,
        long createdAt,
        long updatedAt) {
}
