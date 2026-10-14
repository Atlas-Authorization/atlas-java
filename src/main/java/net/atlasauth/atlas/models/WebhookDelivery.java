package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookDelivery(
        String object,
        String id,
        String eventId,
        int attemptNumber,
        String status,
        Integer httpStatus,
        String responseSnippet,
        Long nextRetryAt,
        Long deliveredAt,
        long createdAt,
        String eventType,
        Integer maxAttempts,
        /** The JSON payload that was sent. */
        JsonNode payload) {
}
