package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/migrations/{source}/imports/public_key}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsImportsGetPublicKeyResponse(
        String object,
        String kid,
        String publicKey,
        String alg,
        String status,
        String fingerprint,
        String compact,
        Long createdAt,
        Long retiredAt) {
}
