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
        long updatedAt,
        /** Strictly increases with every change to the user; webhook payloads carry the same value. */ Long version) {

    /** The previous shape, without {@code version}. */
    public User(String object, String id, String username, String firstName, String lastName, String imageUrl, Map<String, Object> publicMetadata, boolean mfaEnabled, boolean banned, boolean locked, Long lastSignInAt, long createdAt, long updatedAt) {
        this(object, id, username, firstName, lastName, imageUrl, publicMetadata, mfaEnabled, banned, locked, lastSignInAt, createdAt, updatedAt, null);
    }
}
