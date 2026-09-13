package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * A log stream — the instance event feed forwarded to an external sink (generic
 * HTTP, Datadog, Splunk HEC). Destination SECRETS are write-only: encrypted at
 * rest and never returned, so {@code destination} carries only non-secret fields
 * plus {@code has_*} markers.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LogStream(
        String object,
        String id,
        String name,
        String type,
        boolean enabled,
        String status,
        List<String> eventFilter,
        Map<String, Object> destination,
        String cursor,
        int consecutiveFailures,
        String lastError,
        Long lastDeliveredAt,
        long createdAt,
        long updatedAt) {
}
