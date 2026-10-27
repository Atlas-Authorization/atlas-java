package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/oauth_clients/{id}/exchange_policy}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsGetExchangePolicyResponse(
        String object,
        String clientId,
        List<String> allowedAudiences,
        List<String> allowedScopes,
        List<String> idJagAudiences,
        Boolean allowImpersonation,
        Boolean requireMayAct,
        Long maxDelegationDepth,
        Boolean configured) {
}
