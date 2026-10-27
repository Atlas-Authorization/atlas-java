package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/users/search}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersSearchQueryResponse(
        String object,
        List<UsersSearchQueryResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
