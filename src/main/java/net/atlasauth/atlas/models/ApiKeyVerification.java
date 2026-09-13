package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * The verify verdict. Every negative — unknown, malformed, revoked, expired, or a
 * key whose subject was since deleted — resolves to the SAME {@code valid=false}
 * (all other fields null), so a caller learns nothing about which keys exist.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyVerification(
        String object,
        boolean valid,
        String id,
        String subjectType,
        String subjectId,
        Map<String, Object> claims,
        Long lastUsedAt) {
}
