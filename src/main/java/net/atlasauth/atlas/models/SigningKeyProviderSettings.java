package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * Where the token-signing key lives ({@code local}, {@code aws_kms}, {@code gcp_kms} or
 * {@code azure_key_vault}). Never carries credentials. {@code active} is true when the
 * current main key is in this KMS; {@code keyVersion} is the KMS key version tokens are
 * signed with. {@code rotation} is set on a PUT or DELETE.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SigningKeyProviderSettings(
        String object,
        String id,
        String provider,
        Map<String, Object> config,
        boolean active,
        String keyVersion,
        Long lastVerifiedAt,
        Long createdAt,
        Long updatedAt,
        JsonNode rotation,
        boolean deleted) {
}
