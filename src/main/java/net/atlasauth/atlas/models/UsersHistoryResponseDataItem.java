package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersHistoryResponseDataItem(
        String object,
        String id,
        String userId,
        String action,
        Map<String, Object> actor,
        List<Map<String, Object>> changes,
        String reason,
        Long expiresAt,
        Long createdAt) {
}
