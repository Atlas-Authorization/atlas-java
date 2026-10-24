package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The answer to a bulk webhook redelivery ({@code webhook_redelivery}) or replay ({@code webhook_replay}).
 * When {@code hasMore}, {@code nextSince} is the {@code since} that continues the call.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookBulkResult(
        String object,
        String endpointId,
        int queued,
        Integer eventsConsidered,
        boolean hasMore,
        Long nextSince,
        /** events_days, delivery_log_days, replay_window_days. */ JsonNode retention) {
}
