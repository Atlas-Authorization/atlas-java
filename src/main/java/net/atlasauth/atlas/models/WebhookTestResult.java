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
        boolean willRetry,
        /** Always {@code webhook.test}. */
        String eventType,
        /** Why the attempt failed: non_2xx, redirect, timeout, tls, dns, connection_refused, network or url_blocked; null on success. */
        String errorKind) {

    /** The 1.19 shape, without eventType and errorKind. */
    public WebhookTestResult(String object, String deliveryId, String eventId, String status, Integer httpStatus, Long latencyMs, String responseSnippet, String error, String signatureHeaderSent, boolean willRetry) {
        this(object, deliveryId, eventId, status, httpStatus, latencyMs, responseSnippet, error, signatureHeaderSent, willRetry, null, null);
    }
}
