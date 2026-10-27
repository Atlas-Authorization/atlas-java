package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedGrantsListResponseDataItem(
        String object,
        String id,
        String clientId,
        String userId,
        String organizationId,
        String status,
        List<String> scopes,
        String resource,
        List<Map<String, Object>> authorizationDetails,
        Long revision,
        Long createdAt,
        Long updatedAt,
        Long revokedAt,
        String revokedBy) {
}
