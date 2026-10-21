package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookSecret(
        String object,
        String secret,
        /** On a rotation: what happens to the old secret. */ String warning,
        /** On a rotation: the overlap applied, in seconds. */ Integer overlapSeconds,
        /** On a rotation: when the previous secret stops signing deliveries (epoch ms), or null when it already has. */ Long previousSecretExpiresAt) {

    /** The 1.5 shape, without {@code warning}, {@code overlapSeconds}, {@code previousSecretExpiresAt}. */
    public WebhookSecret(String object, String secret) {
        this(object, secret, null, null, null);
    }
}
