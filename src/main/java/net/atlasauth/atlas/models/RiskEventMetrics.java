package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Risk-event counts per level/action/mode over the last {@code days}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiskEventMetrics(
        String object,
        int days,
        int total,
        List<Bucket> buckets) {

    /** One level/action/mode count. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Bucket(String level, String action, String mode, int count) {
    }
}
