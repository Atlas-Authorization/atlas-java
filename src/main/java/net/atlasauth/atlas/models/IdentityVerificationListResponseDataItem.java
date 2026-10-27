package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityVerificationListResponseDataItem(
        String object,
        String id,
        String userId,
        String provider,
        String status,
        String reason,
        String url,
        String decidedBy,
        Long createdAt,
        Long completedAt) {
}
