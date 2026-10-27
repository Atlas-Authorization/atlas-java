package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionsCallsResponseStats(
        @JsonProperty("calls_24h") Long calls24h,
        @JsonProperty("denied_24h") Long denied24h,
        @JsonProperty("failed_24h") Long failed24h,
        @JsonProperty("timeouts_24h") Long timeouts24h,
        Long p95LatencyMs,
        Long lastFailedAt) {
}
