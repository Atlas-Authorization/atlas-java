package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/feature_flags}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FlagsCreateResponse(
        String object,
        String id,
        String key,
        String type,
        String description,
        Boolean enabled,
        Object defaultValue,
        Object offValue,
        List<FlagsCreateResponseRulesItem> rules,
        Long createdAt,
        Long updatedAt) {
}
