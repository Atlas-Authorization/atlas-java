package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/fga/stores/{id}/list-users}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaListUsersResponse(
        String object,
        List<String> users,
        List<String> excludedUsers,
        String continuationToken,
        String authorizationModelId,
        String consistencyToken) {
}
