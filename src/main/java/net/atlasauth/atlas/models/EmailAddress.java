package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailAddress(
        String object,
        String id,
        String emailAddress,
        boolean verified,
        boolean primary,
        long createdAt) {
}
