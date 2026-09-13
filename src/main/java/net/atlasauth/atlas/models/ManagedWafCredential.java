package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Credential config, with secrets reduced to {@code has_*} markers. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedWafCredential(
        String mode,
        String accessKeyId,
        boolean hasSecretAccessKey,
        String roleArn,
        boolean hasExternalId) {
}
