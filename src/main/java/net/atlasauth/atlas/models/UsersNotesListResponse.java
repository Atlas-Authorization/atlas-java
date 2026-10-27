package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/users/{id}/notes}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersNotesListResponse(
        String object,
        List<UsersNotesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
