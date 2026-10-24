package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformInstanceSecret(
        String object,
        /** Deprecated: never sent by the API; read {@link #instanceId()}. */ String id,
        /** The new secret key, revealed exactly once. */ String secret,
        String note,
        String appId,
        String instanceId,
        /** The overlap applied, in seconds. */ Long overlapSeconds,
        /** When the prior keys stop working (epoch ms); null on a hard cut. */ Long previousKeysExpireAt,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public PlatformInstanceSecret(String object, String id, String secret, String note, String appId, String instanceId, Long overlapSeconds, Long previousKeysExpireAt) {
        this(object, id, secret, note, appId, instanceId, overlapSeconds, previousKeysExpireAt, null, null, null, null);
    }


    /** The 1.5 shape, without {@code appId}, {@code instanceId}, {@code overlapSeconds}, {@code previousKeysExpireAt}. */
    public PlatformInstanceSecret(String object, String id, String secret, String note) {
        this(object, id, secret, note, null, null, null, null);
    }
}
