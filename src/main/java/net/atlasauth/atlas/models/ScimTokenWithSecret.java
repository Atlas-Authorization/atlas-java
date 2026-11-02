package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed,
        /** The SCIM 2.0 base URL to give the identity provider with this token. */ String scimBaseUrl,
        /** Non-blocking notes; {@code no_sso_connection} = the organization has no SSO connection yet. */ List<Warning> warnings) {
    /** The 1.19 shape, without scimBaseUrl and warnings. */
    public ScimTokenWithSecret(String object, String id, String name, String organizationId, String connectionId, String prefix, Long lastUsedAt, Long expiresAt, Long revokedAt, long createdAt, String secret, String note, String sealedSecret, String sealedClientSecret, String sealedWebhookSecret, SecretsSealed secretsSealed) {
        this(object, id, name, organizationId, connectionId, prefix, lastUsedAt, expiresAt, revokedAt, createdAt, secret, note, sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed, null, null);
    }

    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public ScimTokenWithSecret(String object, String id, String name, String organizationId, String connectionId, String prefix, Long lastUsedAt, Long expiresAt, Long revokedAt, long createdAt, String secret, String note) {
        this(object, id, name, organizationId, connectionId, prefix, lastUsedAt, expiresAt, revokedAt, createdAt, secret, note, null, null, null, null);
    }

    /** A note on the created token. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Warning(String code, String message) {
    }
}
