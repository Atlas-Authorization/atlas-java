package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/authzen/access/v1/evaluations}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacAuthzenEvaluationsResponse(
        List<AbacAuthzenEvaluationsResponseEvaluationsItem> evaluations,
        Boolean decision) {
}
