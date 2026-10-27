package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/migrations/{source}/bridge}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsGetBridgeResponse(
        String object,
        String source,
        Boolean enabled,
        Long endsAt,
        String mode,
        String preset,
        Map<String, Object> presetVars,
        String issuer,
        String jwksUrl,
        List<String> audience,
        List<String> algorithms,
        Map<String, Object> claimMap,
        Long expiredGraceSeconds,
        List<String> cookieNames,
        Boolean createUsers,
        Boolean hasSharedSecret,
        String exchangePath,
        MigrationsGetBridgeResponseStats stats,
        Long createdAt,
        Long updatedAt) {
}
