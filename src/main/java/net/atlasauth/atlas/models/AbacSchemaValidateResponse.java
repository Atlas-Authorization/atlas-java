package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/abac_schema/validate}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacSchemaValidateResponse(
        String object,
        Boolean valid,
        Boolean schemaChecked,
        List<AbacSchemaGetResponsePolicyIssuesItem> issues) {
}
