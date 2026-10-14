package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPayload(
        String id,
        String type,
        long timestamp,
        String instanceId,
        JsonNode data) {
}
