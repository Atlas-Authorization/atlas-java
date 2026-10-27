package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/active_users}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsActiveUsersResponse(
        String object,
        AnalyticsFunnelResponseWindow window,
        Long dau,
        Long wau,
        Long mau,
        List<AnalyticsActiveUsersResponseDataItem> data) {
}
