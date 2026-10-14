package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformAppSecretRotation(
        String object,
        String appId,
        String secret,
        long overlapSeconds,
        Long previousKeysExpireAt,
        String note) {
}
