package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The reduced shape {@code PUT /v1/roles/:id/permissions} returns. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RolePermissionsResult(
        String object,
        String id,
        String key,
        List<String> permissions,
        List<String> ignored) {
}
