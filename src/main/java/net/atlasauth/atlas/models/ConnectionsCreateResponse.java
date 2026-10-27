package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsCreateResponse(
        String object,
        String id,
        ConnectionsCreateResponseOwner owner,
        Object kind,
        String provider,
        String name,
        List<String> allowedHosts,
        Map<String, Object> config,
        Object status,
        String purpose,
        Object environment,
        Boolean sandbox,
        String externalId,
        Map<String, Object> hostParams,
        String oauthClientId,
        List<String> scopes,
        Long expiresAt,
        Map<String, Object> metadata,
        String authorizedByUserId,
        Long connectedAt,
        Long lastRefreshedAt,
        Long statusChangedAt,
        Object lastError,
        Long refreshFailures,
        Long nextRefreshAt,
        Object externalAccountId,
        Boolean hasSecret,
        Long secretExpiresAt,
        Long previousValidUntil,
        List<Object> allowedMethods,
        Long createdAt,
        Long updatedAt) {
}
