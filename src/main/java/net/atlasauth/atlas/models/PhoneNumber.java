package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PhoneNumber(
        String object,
        String id,
        String phoneNumber,
        boolean verified,
        long createdAt) {
}
