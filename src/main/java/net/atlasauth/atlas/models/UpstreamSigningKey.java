package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * A private_key_jwt signing key Atlas holds for an upstream provider ({@code RS256},
 * {@code PS256} or {@code ES256}; status {@code active}, {@code retiring} or
 * {@code retired}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpstreamSigningKey(
        String object,
        String kid,
        String alg,
        String status,
        Map<String, Object> publicJwk,
        long createdAt,
        Long retiredAt) {
}
