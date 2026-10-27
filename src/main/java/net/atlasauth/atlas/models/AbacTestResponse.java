package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/abac_policies/{id}/test}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacTestResponse(
        String object,
        Boolean passed,
        AbacTestResponseCoverage coverage,
        List<AbacTestResponseResultsItem> results) {
}
