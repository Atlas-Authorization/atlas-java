package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/abac_schema}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacSchemaGetResponse(
        String object,
        Map<String, Object> schema,
        Long version,
        Long updatedAt,
        List<AbacSchemaGetResponsePolicyIssuesItem> policyIssues) {
}
