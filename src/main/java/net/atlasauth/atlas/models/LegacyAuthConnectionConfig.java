package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LegacyAuthConnectionConfig(
        String object,
        String id,
        Boolean enabled,
        String verifyUrl,
        Boolean hasSigningSecret,
        Boolean importOnLogin,
        Long timeoutMs,
        String strategy,
        Long createdAt,
        Long updatedAt,
        Boolean configured) {
}
