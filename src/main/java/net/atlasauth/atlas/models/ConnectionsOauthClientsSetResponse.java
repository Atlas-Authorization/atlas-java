package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code PUT /v1/connections/oauth_clients}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsOauthClientsSetResponse(
        String object,
        String id,
        ConnectionsCreateResponseOwner owner,
        String provider,
        Object environment,
        String clientId,
        Boolean hasSecret,
        List<String> scopes,
        Map<String, Object> hostParams,
        Boolean enabled,
        String callbackUrl,
        Long createdAt,
        Long updatedAt) {
}
