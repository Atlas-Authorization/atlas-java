package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestingToken(
        String object,
        String jwt,
        String tokenUse,
        long expiresIn) {
}
