package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/feature_flags/{key}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FlagsDeleteResponse(
        String object,
        String id,
        String key,
        Boolean deleted) {
}
