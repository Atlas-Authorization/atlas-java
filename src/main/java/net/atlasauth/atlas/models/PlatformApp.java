package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** An app (instance) a platform owns. Never carries a secret except on create/rotate/claim. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformApp(
        String object,
        String id,
        String name,
        String environment,
        String publishableKey,
        List<String> allowedOrigins,
        List<String> tags,
        boolean archived,
        Long archivedAt,
        boolean disabled,
        long createdAt,
        long updatedAt,
        /** The Frontend API host: the default per-app host, or the custom domain once it is active. */ String frontendApiHost,
        /** The {@code iss} the app's session tokens carry; it changes when a custom domain goes active. */ String issuer,
        /** The OpenID discovery document whose {@code issuer} equals {@link #issuer()}. */ String discoveryUrl,
        /** The JWKS holding the app's signing keys. */ String jwksUri,
        /** The declared integration per allowed origin ({@code redirect} or {@code embedded}). */ Map<String, String> allowedOriginIntegrations) {
    /** The 1.4 shape, without {@code frontendApiHost}, {@code issuer}, {@code discoveryUrl}, {@code jwksUri}, {@code allowedOriginIntegrations}. */
    public PlatformApp(String object, String id, String name, String environment, String publishableKey, List<String> allowedOrigins, List<String> tags, boolean archived, Long archivedAt, boolean disabled, long createdAt, long updatedAt) {
        this(object, id, name, environment, publishableKey, allowedOrigins, tags, archived, archivedAt, disabled, createdAt, updatedAt, null, null, null, null, null);
    }
}
