package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A live provider credential. The field is {@code token}, not {@code access_token}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthAccessToken(
        String object,
        String provider,
        String token,
        Long expiresAt,
        List<String> scopes,
        boolean refreshed) {
}
