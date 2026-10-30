package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Where the hosted pages send the browser ({@code GET/PATCH /v1/instance/hosted_pages}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record HostedPagesSettings(
        /** {@code hosted_pages}. */ String object,
        /** {@code /hosted/sign-in} answers 302 here; null serves the hosted page. */ String signInUrl,
        String signUpUrl,
        String userUrl,
        /** The hosted pages these settings apply to. */ Hosted hosted) {

    /** The hosted page URLs. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hosted(String signIn, String signUp, String user) {
    }
}
