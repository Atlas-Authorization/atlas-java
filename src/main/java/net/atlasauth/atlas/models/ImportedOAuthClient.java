package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportedOAuthClient(
        String object,
        String id,
        String clientId,
        String name,
        String logoUrl,
        List<String> redirectUris,
        List<String> allowedScopes,
        List<String> grantTypes,
        String tokenEndpointAuthMethod,
        String secretPrefix,
        Boolean isPublic,
        Boolean firstParty,
        List<String> postLogoutRedirectUris,
        String backchannelLogoutUri,
        Boolean backchannelLogoutSessionRequired,
        String frontchannelLogoutUri,
        Boolean frontchannelLogoutSessionRequired,
        String clientSecret,
        Long createdAt,
        Long updatedAt) {
}
