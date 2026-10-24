package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformSecretKey(
        String object,
        String id,
        String prefix,
        long createdAt,
        Long expiresAt,
        String name,
        /** Provisioning tags (strings, numbers and booleans). */ Map<String, Object> metadata,
        /** Your idempotent-create key. */ String externalRef,
        /** True on a create that sent an {@code external_ref} and got the existing object back (200, no secret). */ Boolean idempotentReplay,
        String appId,
        String instanceId,
        /** The key, shown once on a fresh create (never on an idempotent replay). */ String secret,
        String note,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public PlatformSecretKey(String object, String id, String prefix, long createdAt, Long expiresAt, String name, Map<String, Object> metadata, String externalRef, Boolean idempotentReplay, String appId, String instanceId, String secret, String note) {
        this(object, id, prefix, createdAt, expiresAt, name, metadata, externalRef, idempotentReplay, appId, instanceId, secret, note, null, null, null, null);
    }

    /** The shape before the name, provisioning and create fields. */
    public PlatformSecretKey(String object, String id, String prefix, long createdAt, Long expiresAt) {
        this(object, id, prefix, createdAt, expiresAt, null, null, null, null, null, null, null, null);
    }

}
