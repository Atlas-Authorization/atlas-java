package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * {@code GET /v1/api_keys/{id}/usage} ({@code object: "api_key_usage"}): each cap
 * with its current-window usage, plus the current-window counters of every unit the
 * key has been debited in, capped or not.
 *
 * @param limits the key's caps; null when it has none
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyUsage(
        String object,
        String keyId,
        List<ApiKeyLimit> limits,
        List<ApiKeyLimitState> usage,
        List<ApiKeyUsageCounter> counters) {
}
