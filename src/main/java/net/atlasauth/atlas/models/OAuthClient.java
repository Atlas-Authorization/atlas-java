package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A registered OAuth client (relying application) of this instance. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClient(
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
        boolean isPublic,
        boolean firstParty,
        long createdAt,
        long updatedAt) {
}
