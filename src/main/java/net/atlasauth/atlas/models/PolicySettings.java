package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** {@code sessionClaim}: put {@code pol} (accepted versions of required policies) on session tokens. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySettings(
        String object,
        Boolean sessionClaim) {
}
