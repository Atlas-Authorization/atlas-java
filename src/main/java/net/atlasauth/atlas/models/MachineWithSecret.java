package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MachineWithSecret(
        String object,
        String id,
        String name,
        String organizationId,
        String ownerUserId,
        String status,
        Boolean ownerUsable,
        String effectiveStatus,
        String deviceKey,
        String publicKeyJkt,
        Map<String, Object> metadata,
        Long enrolledAt,
        long createdAt,
        Long updatedAt,
        String secret,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public MachineWithSecret(String object, String id, String name, String organizationId, String ownerUserId, String status, Boolean ownerUsable, String effectiveStatus, String deviceKey, String publicKeyJkt, Map<String, Object> metadata, Long enrolledAt, long createdAt, Long updatedAt, String secret) {
        this(object, id, name, organizationId, ownerUserId, status, ownerUsable, effectiveStatus, deviceKey, publicKeyJkt, metadata, enrolledAt, createdAt, updatedAt, secret, null, null, null, null);
    }

}
