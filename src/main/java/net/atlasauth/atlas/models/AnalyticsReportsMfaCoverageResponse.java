package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/reports/mfa_coverage}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsReportsMfaCoverageResponse(
        String object,
        AnalyticsFunnelResponseWindow window,
        Long totalUsers,
        List<AnalyticsReportsMfaCoverageResponseDataItem> data) {
}
