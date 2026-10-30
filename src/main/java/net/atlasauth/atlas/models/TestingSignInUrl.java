package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A one-time URL that signs a browser in as a user ({@code POST /v1/testing/sign_in_url}, development instances only). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TestingSignInUrl(
        /** {@code testing_sign_in_url}. */ String object,
        String id,
        String userId,
        String ticket,
        /** Open this in the browser under test; it lands on {@code redirectUrl} signed in. */ String url,
        String redirectUrl,
        /** Lifetime in seconds. */ long expiresIn,
        /** ISO 8601. */ String expiresAt) {
}
