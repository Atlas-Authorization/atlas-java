package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountlessApp(
        String object,
        String id,
        String name,
        String environment,
        String publishableKey,
        String claimToken,
        long expiresAt,
        String note) {
}
