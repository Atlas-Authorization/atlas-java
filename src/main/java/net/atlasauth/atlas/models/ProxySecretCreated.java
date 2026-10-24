package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Create / rotate result of the trusted reverse proxy secret: the secret, shown only here. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProxySecretCreated(
        String object,
        boolean set,
        Long createdAt,
        /** After a rotation: when the replaced secret stops working (epoch ms), or null. */ Long previousExpiresAt,
        String secretHeader,
        String clientIpHeader,
        /** {@code aps_...}, returned once. Store it in your proxy's environment; Atlas keeps only a hash. */ String secret,
        /** true when an existing secret was replaced. */ boolean rotated,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public ProxySecretCreated(String object, boolean set, Long createdAt, Long previousExpiresAt, String secretHeader, String clientIpHeader, String secret, boolean rotated) {
        this(object, set, createdAt, previousExpiresAt, secretHeader, clientIpHeader, secret, rotated, null, null, null, null);
    }

}
