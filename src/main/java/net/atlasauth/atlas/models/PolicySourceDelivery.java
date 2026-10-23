package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** One fetch, test, poll or event exchanged with the policy source. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySourceDelivery(
        String object,
        String id,
        String direction,
        String kind,
        String status,
        Integer httpStatus,
        String error,
        String eventType,
        String policy,
        String version,
        String url,
        Map<String, Object> changes,
        Integer latencyMs,
        Long createdAt) {
}
