package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A machine's authorization for a resource server: it may obtain tokens for that API, up to {@code scopes}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MachineGrant(
        String object,
        String id,
        String machineId,
        String resourceServerId,
        List<String> scopes,
        long createdAt,
        long updatedAt) {
}
