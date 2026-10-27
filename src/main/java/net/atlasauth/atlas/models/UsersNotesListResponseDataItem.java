package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersNotesListResponseDataItem(
        String object,
        String id,
        String userId,
        String body,
        Boolean pinned,
        Map<String, Object> author,
        Long createdAt,
        Long updatedAt) {
}
