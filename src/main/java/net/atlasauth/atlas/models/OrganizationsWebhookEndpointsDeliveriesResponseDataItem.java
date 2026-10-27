package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationsWebhookEndpointsDeliveriesResponseDataItem(
        String object,
        String id,
        String eventId,
        String eventType,
        Long latencyMs,
        String traceId,
        Long attemptNumber,
        Long maxAttempts,
        Object payload,
        String status,
        Long httpStatus,
        String error,
        String location,
        String responseSnippet,
        Long nextRetryAt,
        Long deliveredAt,
        Long createdAt) {
}
