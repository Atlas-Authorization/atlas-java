package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** "Sign in with Atlas" on the instance ({@code GET/PATCH /v1/instance/sign_in_with_atlas}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SignInWithAtlasSettings(
        /** {@code sign_in_with_atlas}. */ String object,
        /** Whether the embedded and hosted sign-in show "Continue with Atlas". */ boolean enabled,
        /** The SSO connection the button starts; null when off. */ String connectionId,
        /** {@code instance_toggle} (Atlas created the connection and its Atlas ID app), {@code linked} (an existing connection of yours) or null. */ String managedVia,
        String connectionStatus,
        /** Whether this deployment can offer Sign in with Atlas at all. */ boolean available,
        /** The Atlas issuer the connection must point at. */ String issuer,
        /** Active OIDC connections to that issuer that could be linked. */ List<Candidate> candidates) {

    /** An OIDC connection to the Atlas issuer that could be linked. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(String id, String clientId, boolean managedByThisInstance) {
    }
}
