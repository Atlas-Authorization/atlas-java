package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Usage(
        String object,
        UsageWindow window,
        UsagePreviousWindow previousWindow,
        List<UsageMetrics> metrics,
        UsageMonthlyActiveUsers monthlyActiveUsers) {
}
