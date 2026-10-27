package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/authz_decision_settings}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthzDecisionsSettingsGetResponse(
        String object,
        Boolean enabled,
        Double sampleRateAllow,
        Double sampleRateDeny,
        String inputMode,
        List<String> redact,
        Boolean shipToEvents,
        Long retentionDays) {
}
