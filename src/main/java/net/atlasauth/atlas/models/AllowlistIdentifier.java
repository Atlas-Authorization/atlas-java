package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AllowlistIdentifier(
        String object,
        String id,
        String identifier,
        long createdAt) {
}
