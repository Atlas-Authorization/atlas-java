package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/migrations/{source}/exchange}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsExchangeResponse(
        String object,
        String id,
        String sessionId,
        String userId,
        String jwt,
        String refreshToken,
        Long expiresIn,
        String status,
        String identifier,
        List<String> supportedFirstFactors,
        Object createdSessionId,
        Long expiresAt) {
}
