package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaGrantsListResponseDataItem(
        String object,
        String id,
        String userId,
        String entitlementId,
        String requestId,
        String source,
        String status,
        Long grantedAt,
        Long expiresAt,
        Long revokedAt,
        String revokedReason,
        String revokedBy,
        Boolean adopted) {
}
