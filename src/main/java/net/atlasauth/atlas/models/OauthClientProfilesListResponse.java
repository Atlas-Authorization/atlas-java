package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/oauth_client_profiles}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OauthClientProfilesListResponse(
        String object,
        List<OauthClientProfilesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
