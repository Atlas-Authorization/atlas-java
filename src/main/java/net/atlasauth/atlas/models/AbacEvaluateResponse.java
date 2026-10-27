package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/abac/evaluate}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacEvaluateResponse(
        String object,
        String decision,
        List<String> reasons,
        List<String> errors) {
}
