package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/feature_flags/evaluate}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FlagsEvaluateResponse(
        String object,
        Map<String, Object> subject,
        Map<String, Object> flags) {
}
