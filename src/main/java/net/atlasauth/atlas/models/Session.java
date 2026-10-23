package net.atlasauth.atlas.models;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A session as returned by the {@code /v1/sessions} routes. {@code idleExpiresAt} is the
 * epoch ms the session ends from inactivity unless there is activity first (same value
 * as {@code abandonAt}); see {@link net.atlasauth.atlas.resources.Sessions#touch(String)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Session(
        String object,
        String id,
        String userId,
        String status,
        String lastActiveOrganizationId,
        String impersonatedBy,
        long lastActiveAt,
        long expireAt,
        long abandonAt,
        long createdAt,
        Long idleExpiresAt,
        /** Custom per-session data your backend (or a post_login Action) attached; null when none. */ Map<String, Object> customData) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public Session(String object, String id, String userId, String status, String lastActiveOrganizationId, String impersonatedBy, long lastActiveAt, long expireAt, long abandonAt, long createdAt, Long idleExpiresAt) {
        this(object, id, userId, status, lastActiveOrganizationId, impersonatedBy, lastActiveAt, expireAt, abandonAt, createdAt, idleExpiresAt, null);
    }

    /** The 1.1 shape, without {@code idleExpiresAt}. */
    public Session(String object, String id, String userId, String status, String lastActiveOrganizationId,
                   String impersonatedBy, long lastActiveAt, long expireAt, long abandonAt, long createdAt) {
        this(object, id, userId, status, lastActiveOrganizationId, impersonatedBy, lastActiveAt, expireAt, abandonAt,
                createdAt, null);
    }
}
