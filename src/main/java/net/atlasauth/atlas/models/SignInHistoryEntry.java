package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SignInHistoryEntry(
        String object,
        String id,
        String method,
        String provider,
        String providerEmail,
        String ip,
        String impersonatedBy,
        Long createdAt) {
}
