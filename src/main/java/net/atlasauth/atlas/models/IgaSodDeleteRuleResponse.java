package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/iga/sod_rules/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaSodDeleteRuleResponse(
        String object,
        String id,
        Boolean deleted) {
}
