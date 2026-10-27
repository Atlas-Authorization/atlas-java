package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/actions/{id}/calls}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionsCallsResponse(
        String object,
        List<ActionsCallsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor,
        ActionsCallsResponseStats stats) {
}
