package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An actor token for impersonation. The {@code token} is a one-time opaque
 * secret revealed only once, at creation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActorToken(
        String object,
        String id,
        String userId,
        ActorTokenActor actor,
        String token,
        long expiresIn) {
}
