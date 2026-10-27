package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** The result of {@code POST /v1/fraud/lookup}; {@code verdict} is {@code allow}/{@code challenge}/{@code block}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FraudLookup(
        String object,
        String verdict,
        List<String> reasons,
        List<String> matchedRules,
        String deviceId,
        Map<String, Object> signals) {
}
