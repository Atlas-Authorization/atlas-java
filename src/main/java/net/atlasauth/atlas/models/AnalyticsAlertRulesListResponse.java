package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/alert_rules}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsAlertRulesListResponse(
        String object,
        List<AnalyticsAlertRulesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor,
        Map<String, Object> metrics) {
}
