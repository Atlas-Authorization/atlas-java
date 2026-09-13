package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The impersonation actor of an actor token — records who is acting as the user. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActorTokenActor(
        String sub) {
}
