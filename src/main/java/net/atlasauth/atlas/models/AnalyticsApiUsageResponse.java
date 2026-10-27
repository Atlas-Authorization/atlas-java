package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/api_usage}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsApiUsageResponse(
        String object,
        Long windowMinutes,
        Long baselineHours,
        Long generatedAt,
        List<AnalyticsApiUsageResponseDataItem> data) {
}
