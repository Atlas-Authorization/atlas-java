package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/feature_flags}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FlagsListResponse(
        String object,
        List<FlagsCreateResponse> data,
        Boolean hasMore,
        String nextCursor) {
}
