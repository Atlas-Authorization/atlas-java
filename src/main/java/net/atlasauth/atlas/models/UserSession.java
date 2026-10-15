package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A user's session row as returned by {@code GET /v1/users/:id/sessions}.
 *
 * @param lastActiveOrganizationId the organization active in the session, or null
 * @param impersonatedBy           the actor impersonating the user in this session, or null
 * @param idleExpiresAt            epoch ms the session ends from inactivity unless there is activity
 *                                 first (same value as {@code abandonAt}); nullable
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserSession(
        String object,
        String id,
        String userId,
        String status,
        long lastActiveAt,
        long expireAt,
        long abandonAt,
        long createdAt,
        String lastActiveOrganizationId,
        String impersonatedBy,
        Long idleExpiresAt) {

    /** The 1.2 shape, without {@code lastActiveOrganizationId}, {@code impersonatedBy} and {@code idleExpiresAt}. */
    public UserSession(String object, String id, String userId, String status, long lastActiveAt, long expireAt,
                       long abandonAt, long createdAt) {
        this(object, id, userId, status, lastActiveAt, expireAt, abandonAt, createdAt, null, null, null);
    }
}
