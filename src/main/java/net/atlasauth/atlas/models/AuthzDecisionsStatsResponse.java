package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/authz_decisions/stats}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthzDecisionsStatsResponse(
        String object,
        Long since,
        Long until,
        Long recorded,
        Long estimatedTotal,
        Map<String, Object> bySource,
        AuthzDecisionsStatsResponseLatencyMs latencyMs) {
}
