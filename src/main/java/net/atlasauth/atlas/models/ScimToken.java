package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScimToken(
        String object,
        String id,
        String name,
        String organizationId,
        String connectionId,
        String prefix,
        Long lastUsedAt,
        Long expiresAt,
        Long revokedAt,
        long createdAt) {
}
