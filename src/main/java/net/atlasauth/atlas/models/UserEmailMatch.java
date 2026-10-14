package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserEmailMatch(
        String emailAddress,
        String userId,
        boolean verified) {
}
