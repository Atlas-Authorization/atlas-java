package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformAppTransfer(
        String object,
        String id,
        String transferToken,
        long expiresAt,
        String note) {
}
