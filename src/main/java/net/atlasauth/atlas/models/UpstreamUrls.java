package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The URLs to register at an upstream OAuth / OIDC provider. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpstreamUrls(
        /** The client JWKS URL (private_key_jwt). */ String clientJwksUri,
        String backchannelLogoutUri,
        String frontchannelLogoutUri,
        String postLogoutRedirectUri,
        String initiateLoginUri) {
}
