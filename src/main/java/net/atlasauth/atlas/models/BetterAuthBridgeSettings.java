package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The Better Auth session bridge settings ({@code object: "better_auth_bridge"}); never the secret. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BetterAuthBridgeSettings(
        String object,
        Boolean enabled,
        Long endsAt,
        String cookiePrefix,
        LiveFallback liveFallback,
        String exchangePath,
        Stats stats,
        Long createdAt,
        Long updatedAt) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LiveFallback(Boolean enabled, String baseUrl, Boolean hasSecret) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Stats(Long exchanged, Long fromImport, Long fromLive) {
    }
}
