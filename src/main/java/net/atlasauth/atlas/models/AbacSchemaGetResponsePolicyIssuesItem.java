package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacSchemaGetResponsePolicyIssuesItem(
        String severity,
        Long policy,
        String rule,
        Map<String, Object> clause,
        String message,
        Long offset,
        Long line,
        Long column,
        String sourceName) {
}
