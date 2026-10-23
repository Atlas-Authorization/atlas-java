package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A SAML connection's SP encryption key pair — certificates only. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SamlEncryptionKey(
        String object,
        String connectionId,
        /** The current certificate (PEM), published in SP metadata; null when encryption is off. */ String certificate,
        /** The previous certificate during a rotation. */ String previousCertificate,
        /** When the current key was installed (epoch ms). */ Long rotatedAt,
        boolean requireEncryptedAssertions) {
}
