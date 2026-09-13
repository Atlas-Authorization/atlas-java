package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** The mint response: the key plus its secret, revealed exactly once. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyWithSecret(
        String object,
        String id,
        String subjectType,
        String subjectId,
        String name,
        String prefix,
        Map<String, Object> claims,
        Long lastUsedAt,
        Long expiresAt,
        Long revokedAt,
        long createdAt,
        /** The {@code ak_} secret, revealed only here. */
        String secret,
        String note) {
}
