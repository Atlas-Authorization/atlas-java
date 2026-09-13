package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A session as returned by the {@code /v1/sessions} routes. */
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
        long createdAt) {
}
