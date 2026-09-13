package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BlocklistIdentifier(
        String object,
        String id,
        String identifier,
        long createdAt) {
}
