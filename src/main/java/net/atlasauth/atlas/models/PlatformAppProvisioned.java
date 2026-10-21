package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformAppProvisioned(
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
        String secret,
        String note,
        PlatformAppWithSecret siblingInstance,
        /** The Frontend API host: the default per-app host, or the custom domain once it is active. */ String frontendApiHost,
        /** The {@code iss} the app's session tokens carry; it changes when a custom domain goes active. */ String issuer,
        /** The OpenID discovery document whose {@code issuer} equals {@link #issuer()}. */ String discoveryUrl,
        /** The JWKS holding the app's signing keys. */ String jwksUri,
        /** The declared integration per allowed origin ({@code redirect} or {@code embedded}). */ Map<String, String> allowedOriginIntegrations) {
    /** The 1.4 shape, without {@code frontendApiHost}, {@code issuer}, {@code discoveryUrl}, {@code jwksUri}, {@code allowedOriginIntegrations}. */
    public PlatformAppProvisioned(String object, String id, String name, String environment, String publishableKey, List<String> allowedOrigins, List<String> tags, boolean archived, Long archivedAt, boolean disabled, long createdAt, long updatedAt, String secret, String note, PlatformAppWithSecret siblingInstance) {
        this(object, id, name, environment, publishableKey, allowedOrigins, tags, archived, archivedAt, disabled, createdAt, updatedAt, secret, note, siblingInstance, null, null, null, null, null);
    }
}
