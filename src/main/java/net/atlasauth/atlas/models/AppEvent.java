package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * A published application event ({@code /v1/app_events}). On a publish, {@code idempotentReplay} is true when
 * an earlier publish with the same key is returned (nothing new was queued); {@code deliveriesQueued} and
 * {@code endpointIds} are set on the first publish only. All three are null on a read.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppEvent(
        /** {@code app_event}. */ String object,
        /** {@code evt_} id; the {@code webhook-id} header of every delivery of this event. */ String id,
        String type,
        String organizationId,
        Map<String, Object> data,
        String idempotencyKey,
        long createdAt,
        Boolean idempotentReplay,
        Integer deliveriesQueued,
        List<String> endpointIds) {
}
