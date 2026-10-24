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
        String note,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public ScimTokenWithSecret(String object, String id, String name, String organizationId, String connectionId, String prefix, Long lastUsedAt, Long expiresAt, Long revokedAt, long createdAt, String secret, String note) {
        this(object, id, name, organizationId, connectionId, prefix, lastUsedAt, expiresAt, revokedAt, createdAt, secret, note, null, null, null, null);
    }

}
