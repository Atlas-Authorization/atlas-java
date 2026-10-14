package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Consent(
        String object,
        String id,
        String policy,
        String version,
        String status,
        String source,
        JsonNode metadata,
        long createdAt) {
}
