package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/openfga/stores/{store}/list-users}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFgaListUsersResponse(
        List<OpenFgaListUsersResponseUsersItem> users,
        List<OpenFgaListUsersResponseExcludedUsersItem> excludedUsers,
        String continuationToken) {
}
