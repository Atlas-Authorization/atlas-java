package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/tokens/batch}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsTokensResponse(
        String object,
        List<ConnectionsTokensResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
