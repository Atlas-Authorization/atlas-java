package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** A user as the BAPI serves it. {@code private_metadata} is never returned. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        String object,
        String id,
        String username,
        String firstName,
        String lastName,
        String imageUrl,
        Map<String, Object> publicMetadata,
        boolean mfaEnabled,
        boolean banned,
        boolean locked,
        Long lastSignInAt,
        long createdAt,
        long updatedAt) {
}
