package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The session after a keep-alive touch ({@code POST /v1/sessions/{id}/touch}).
 *
 * @param idleExpiresAt epoch ms the session ends from inactivity unless there is
 *                      activity first (same value as {@code abandonAt}); never past {@code expireAt}
 * @param extended      true when this touch moved the inactivity window; false when a
 *                      touch within the last 60 seconds already did (the state is unchanged)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionTouch(
        String object,
        String id,
        String userId,
        String status,
        String lastActiveOrganizationId,
        String impersonatedBy,
        long lastActiveAt,
        long expireAt,
        long abandonAt,
        long idleExpiresAt,
        long createdAt,
        boolean extended) {
}
