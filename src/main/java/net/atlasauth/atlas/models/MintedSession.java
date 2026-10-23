package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * A freshly-minted session. Unlike a listed {@link Session}, this carries the
 * bearer {@code jwt} and {@code refreshToken} in-body — returned by design for
 * headless use. Store the refresh token to keep the session alive.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MintedSession(
        String object,
        String id,
        String userId,
        String jwt,
        String refreshToken,
        long expiresIn,
        Map<String, Object> impersonatedBy,
        /** The custom data attached at creation, if any. */ Map<String, Object> customData) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public MintedSession(String object, String id, String userId, String jwt, String refreshToken, long expiresIn, Map<String, Object> impersonatedBy) {
        this(object, id, userId, jwt, refreshToken, expiresIn, impersonatedBy, null);
    }
}
