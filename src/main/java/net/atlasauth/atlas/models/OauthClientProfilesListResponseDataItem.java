package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OauthClientProfilesListResponseDataItem(
        String object,
        String name,
        Boolean builtin,
        String description,
        Boolean requirePkce,
        Boolean requirePar,
        String senderConstraint,
        Boolean requireSignedRequest,
        List<String> tokenEndpointAuthMethods,
        List<String> signingAlgs,
        Boolean httpsRedirectsOnly,
        List<String> allowedClaims) {
}
