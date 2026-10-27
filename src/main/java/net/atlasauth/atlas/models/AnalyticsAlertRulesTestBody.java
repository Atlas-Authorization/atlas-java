package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/analytics/alert_rules/{id}/test}. */
public final class AnalyticsAlertRulesTestBody {
    @JsonProperty("notify")
    public Boolean notifyValue;

    public AnalyticsAlertRulesTestBody() {
    }

    public AnalyticsAlertRulesTestBody notifyValue(Boolean v) { this.notifyValue = v; return this; }
}
