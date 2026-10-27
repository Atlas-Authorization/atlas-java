package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/organizations/{id}/connected_apps}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationsListConnectedAppsResponse(
        String object,
        List<OrganizationsListConnectedAppsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
