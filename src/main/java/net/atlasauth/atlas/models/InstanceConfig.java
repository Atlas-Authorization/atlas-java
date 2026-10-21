package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * The tenant's environment: its publishable key, frontend API host, allowed
 * origins and auth config. The signing private key is never returned.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceConfig(
        String object,
        String id,
        String environment,
        String publishableKey,
        String frontendApiHost,
        List<String> allowedOrigins,
        Map<String, Object> authConfig,
        long createdAt,
        /** The {@code iss} the instance's session tokens carry; it changes when a custom domain goes active, so read it rather than hard-coding it. */ String issuer,
        /** The OpenID discovery document whose {@code issuer} equals {@link #issuer()}. */ String discoveryUrl,
        /** The JWKS holding the instance's signing keys. */ String jwksUri,
        /** The declared integration per allowed origin ({@code redirect} or {@code embedded}). */ Map<String, String> allowedOriginIntegrations) {
    /** The 1.4 shape, without {@code issuer}, {@code discoveryUrl}, {@code jwksUri}, {@code allowedOriginIntegrations}. */
    public InstanceConfig(String object, String id, String environment, String publishableKey, String frontendApiHost, List<String> allowedOrigins, Map<String, Object> authConfig, long createdAt) {
        this(object, id, environment, publishableKey, frontendApiHost, allowedOrigins, authConfig, createdAt, null, null, null, null);
    }
}
