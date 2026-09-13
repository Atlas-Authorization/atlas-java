package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A user's session row as returned by {@code GET /v1/users/:id/sessions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserSession(
        String object,
        String id,
        String userId,
        String status,
        long lastActiveAt,
        long expireAt,
        long abandonAt,
        long createdAt) {
}
