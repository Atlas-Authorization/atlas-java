package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacTestResponseCoverage(
        String object,
        Long tests,
        List<Map<String, Object>> rules,
        Map<String, Object> summary,
        List<String> gaps) {
}
