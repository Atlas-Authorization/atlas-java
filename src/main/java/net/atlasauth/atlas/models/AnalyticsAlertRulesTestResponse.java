package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/analytics/alert_rules/{id}/test}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsAlertRulesTestResponse(
        String object,
        Double value,
        Long sample,
        AnalyticsAlertRulesTestResponseSubject subject,
        Boolean notified,
        String error) {
}
