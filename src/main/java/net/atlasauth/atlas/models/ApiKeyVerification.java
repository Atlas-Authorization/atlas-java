package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * The verify verdict. Every negative — unknown, malformed, revoked, expired, or a
 * key whose subject was since deleted — resolves to the SAME {@code valid=false}
 * (all other fields null), so a caller learns nothing about which keys exist.
 * A valid verdict also carries the key's {@code name}, {@code scopes},
 * {@code constraints}, spend caps ({@code limits}) and their current-window {@code usage}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyVerification(
        String object,
        boolean valid,
        String id,
        String subjectType,
        String subjectId,
        Map<String, Object> claims,
        Long lastUsedAt,
        String name,
        List<String> scopes,
        Map<String, Object> constraints,
        List<ApiKeyLimit> limits,
        List<ApiKeyLimitState> usage) {

    /** The 1.1 shape, without name, scopes, constraints or spend caps. */
    public ApiKeyVerification(String object, boolean valid, String id, String subjectType, String subjectId,
                              Map<String, Object> claims, Long lastUsedAt) {
        this(object, valid, id, subjectType, subjectId, claims, lastUsedAt, null, null, null, null, null);
    }
}
