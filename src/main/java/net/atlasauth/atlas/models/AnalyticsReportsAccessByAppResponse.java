package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/reports/access_by_app}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsReportsAccessByAppResponse(
        String object,
        AnalyticsFunnelResponseWindow window,
        List<AnalyticsReportsAccessByAppResponseDataItem> data) {
}
