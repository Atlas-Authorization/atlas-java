package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * A custom upstream OAuth 2.0 / OpenID Connect provider. The client secret is never
 * returned ({@code hasSecret}). The URL fields are what to register at the provider.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomOAuthProvider(
        String object,
        String id,
        String providerKey,
        String displayName,
        String iconUrl,
        /** oidc or oauth2. */ String protocol,
        String issuer,
        String discoveryUrl,
        String authorizationEndpoint,
        String tokenEndpoint,
        String userinfoEndpoint,
        String jwksUri,
        String endSessionEndpoint,
        List<String> scopes,
        String scopeSeparator,
        boolean pkce,
        Map<String, String> authorizationParams,
        Map<String, String> tokenParams,
        /** header or query. */ String userinfoAuth,
        String tokenEndpointAuthMethod,
        String clientAssertionAudience,
        /** merged, userinfo or id_token. */ String profileSource,
        JsonNode profileMapping,
        JsonNode logout,
        JsonNode idpInitiatedLogin,
        String clientId,
        boolean hasSecret,
        boolean enabled,
        boolean allowSignIn,
        boolean allowSignUp,
        /** Register at the provider as the redirect / callback URI. */ String redirectUri,
        String clientJwksUri,
        String backchannelLogoutUri,
        String frontchannelLogoutUri,
        String postLogoutRedirectUri,
        String initiateLoginUri,
        List<UpstreamSigningKey> signingKeys,
        long createdAt,
        long updatedAt) {
}
