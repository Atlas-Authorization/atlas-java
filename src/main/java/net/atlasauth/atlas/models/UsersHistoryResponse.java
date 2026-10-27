package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/users/{id}/history}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersHistoryResponse(
        String object,
        List<UsersHistoryResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
