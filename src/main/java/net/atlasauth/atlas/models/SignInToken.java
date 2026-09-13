package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A one-time sign-in token minted for a user. The token always lives 60 seconds. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SignInToken(
        String object,
        String userId,
        String token,
        long expiresIn) {
}
