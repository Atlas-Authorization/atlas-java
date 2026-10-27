package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/oauth_clients/{id}/trusted_issuers}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsTrustedIssuersListResponse(
        String object,
        List<OAuthClientsTrustedIssuersListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
