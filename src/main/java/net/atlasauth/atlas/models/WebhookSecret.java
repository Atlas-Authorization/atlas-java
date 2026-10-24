package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookSecret(
        String object,
        String secret,
        /** On a rotation: what happens to the old secret. */ String warning,
        /** On a rotation: the overlap applied, in seconds. */ Integer overlapSeconds,
        /** On a rotation: when the previous secret stops signing deliveries (epoch ms), or null when it already has. */ Long previousSecretExpiresAt,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public WebhookSecret(String object, String secret, String warning, Integer overlapSeconds, Long previousSecretExpiresAt) {
        this(object, secret, warning, overlapSeconds, previousSecretExpiresAt, null, null, null, null);
    }


    /** The 1.5 shape, without {@code warning}, {@code overlapSeconds}, {@code previousSecretExpiresAt}. */
    public WebhookSecret(String object, String secret) {
        this(object, secret, null, null, null);
    }
}
