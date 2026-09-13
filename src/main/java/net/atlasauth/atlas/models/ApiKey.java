package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * An end-user API key. A tenant mints these for its own users/organizations and
 * later asks Atlas to verify one a subject presented. Only a hash is stored, so the
 * {@code ak_} secret is shown exactly once at mint (see {@link ApiKeyWithSecret});
 * a read reports only the {@code prefix}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKey(
        String object,
        String id,
        /** "user" or "organization". */
        String subjectType,
        String subjectId,
        String name,
        /** Enough to tell two keys apart in a listing; not enough to use one. */
        String prefix,
        Map<String, Object> claims,
        Long lastUsedAt,
        Long expiresAt,
        Long revokedAt,
        long createdAt) {
}
