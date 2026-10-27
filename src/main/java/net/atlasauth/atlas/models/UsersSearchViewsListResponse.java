package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/users/search/views}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersSearchViewsListResponse(
        String object,
        List<UsersSearchViewsListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
