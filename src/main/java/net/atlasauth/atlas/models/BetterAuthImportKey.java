package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The instance's Better Auth import key: seal the export to it ({@code compact} is the
 * {@code kid:public_key} form the exporter's {@code --public-key} takes).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BetterAuthImportKey(
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
