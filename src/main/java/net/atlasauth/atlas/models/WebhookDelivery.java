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
        JsonNode payload,
        /** webhook_redirect_not_followed (see location), webhook_http_error, webhook_timeout, webhook_network_error or webhook_url_blocked. */ String error,
        /** The absolute Location of a 3xx answer. Not followed. */ String location,
        /** How long the receiver took to answer, in milliseconds. */ Long latencyMs) {

    /** The 1.14 shape, without {@code latencyMs}. */
    public WebhookDelivery(String object, String id, String eventId, int attemptNumber, String status, Integer httpStatus, String responseSnippet, Long nextRetryAt, Long deliveredAt, long createdAt, String eventType, Integer maxAttempts, JsonNode payload, String error, String location) {
        this(object, id, eventId, attemptNumber, status, httpStatus, responseSnippet, nextRetryAt, deliveredAt, createdAt, eventType, maxAttempts, payload, error, location, null);
    }

    /** The previous shape, without {@code error}, {@code location}. */
    public WebhookDelivery(String object, String id, String eventId, int attemptNumber, String status, Integer httpStatus, String responseSnippet, Long nextRetryAt, Long deliveredAt, long createdAt, String eventType, Integer maxAttempts, JsonNode payload) {
        this(object, id, eventId, attemptNumber, status, httpStatus, responseSnippet, nextRetryAt, deliveredAt, createdAt, eventType, maxAttempts, payload, null, null, null);
    }
}
