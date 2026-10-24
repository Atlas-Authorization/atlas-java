package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformAppSecretRotation(
        String object,
        String appId,
        String secret,
        long overlapSeconds,
        Long previousKeysExpireAt,
        String note,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public PlatformAppSecretRotation(String object, String appId, String secret, long overlapSeconds, Long previousKeysExpireAt, String note) {
        this(object, appId, secret, overlapSeconds, previousKeysExpireAt, note, null, null, null, null);
    }

}
