package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/journey_funnel}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsJourneyFunnelResponse(
        String object,
        AnalyticsFunnelResponseWindow window,
        String journeyId,
        String journeyName,
        List<AnalyticsJourneyFunnelResponseDataItem> data) {
}
