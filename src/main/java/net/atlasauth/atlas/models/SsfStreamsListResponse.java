package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/ssf/streams}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsfStreamsListResponse(
        String object,
        List<SsfStreamsListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
