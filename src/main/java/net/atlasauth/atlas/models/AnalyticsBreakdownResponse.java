package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/breakdown}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsBreakdownResponse(
        String object,
        AnalyticsFunnelResponseWindow window,
        String dimension,
        Long total,
        List<AnalyticsBreakdownResponseDataItem> data) {
}
