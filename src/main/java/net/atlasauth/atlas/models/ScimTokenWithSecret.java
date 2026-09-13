package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Create reveals the usable secret exactly once — {@link ScimToken} plus the secret. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScimTokenWithSecret(
        String object,
        String id,
        String name,
        String organizationId,
        String connectionId,
        String prefix,
        Long lastUsedAt,
        Long expiresAt,
        Long revokedAt,
        long createdAt,
        String secret,
        String note) {
}
