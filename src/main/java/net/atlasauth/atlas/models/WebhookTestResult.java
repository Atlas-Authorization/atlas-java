package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The inline result of {@code POST /v1/webhook_endpoints/{id}/test?wait=}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookTestResult(
        String object,
        String deliveryId,
        String eventId,
        String status,
        Integer httpStatus,
        Long latencyMs,
        String responseSnippet,
        String error,
        String signatureHeaderSent,
        boolean willRetry) {
}
