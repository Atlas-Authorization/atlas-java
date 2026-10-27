package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/reports/inactive_users}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsReportsInactiveUsersResponse(
        String object,
        AnalyticsFunnelResponseWindow window,
        Long inactiveDays,
        String cutoff,
        Boolean hasMore,
        String nextCursor,
        List<AnalyticsReportsInactiveUsersResponseDataItem> data) {
}
