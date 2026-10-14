package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformSecretKey(
        String object,
        String id,
        String prefix,
        long createdAt,
        Long expiresAt) {
}
