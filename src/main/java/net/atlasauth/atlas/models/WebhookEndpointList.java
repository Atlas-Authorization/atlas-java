package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * {@code GET /v1/webhook_endpoints} in full: the endpoints plus the event types you
 * can subscribe to and the retry schedule every delivery follows.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEndpointList(
        String object,
        List<WebhookEndpoint> data,
        Boolean hasMore,
        /** Every event type an endpoint can list in {@code enabled_events}. */
        List<String> availableEvents,
        /** Delay before each retry, in milliseconds. */
        List<Long> retryScheduleMs,
        int maxAttempts,
        /** The same events as availableEvents, each described. */ List<WebhookEventCatalogEntry> eventCatalog,
        /** Every value relevantFor can hold. */ List<String> eventAudiences) {

    /** The previous shape, without {@code eventCatalog}, {@code eventAudiences}. */
    public WebhookEndpointList(String object, List<WebhookEndpoint> data, Boolean hasMore, List<String> availableEvents, List<Long> retryScheduleMs, int maxAttempts) {
        this(object, data, hasMore, availableEvents, retryScheduleMs, maxAttempts, null, null);
    }
}
