package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/identity_verifications}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityVerificationListResponse(
        String object,
        List<IdentityVerificationListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
