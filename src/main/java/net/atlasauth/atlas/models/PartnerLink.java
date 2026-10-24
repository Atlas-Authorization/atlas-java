package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A credential an integrated partner service holds on this instance. {@code clientSecret} is set once, on
 * create and rotate; a revoke carries {@code revoked}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PartnerLink(
        String object,
        String id,
        String clientId,
        /** Any lowercase id; cookiemunch, chatly, forumulate and referalli are presets. */ String partner,
        String name,
        boolean preset,
        String resource,
        List<String> scopes,
        String clientSecretPrefix,
        String tokenEndpoint,
        /** active, expired or revoked. */ String status,
        Long expiresAt,
        Long revokedAt,
        Long lastUsedAt,
        String lastUsedIp,
        @JsonProperty("calls_30d") int calls30d,
        long createdAt,
        String clientSecret,
        Boolean revoked) {
}
