package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
        long createdAt) {
}
