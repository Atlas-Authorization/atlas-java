package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationsListConnectedAppsResponseDataItem(
        String object,
        String id,
        String organizationId,
        String userId,
        String clientId,
        String clientName,
        String scope,
        Long connectedAt) {
}
