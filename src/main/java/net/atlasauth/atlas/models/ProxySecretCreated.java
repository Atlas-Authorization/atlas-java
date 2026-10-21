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
        /** true when an existing secret was replaced. */ boolean rotated) {
}
