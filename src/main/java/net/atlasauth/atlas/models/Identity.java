package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One sign-in identity on a user: the base Atlas anchor, or a linked provider. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Identity(
        String object,
        String id,
        String type,
        String provider,
        String providerUserId,
        String email,
        Boolean emailVerified,
        boolean isPrimary,
        Boolean hasPassword) {
}
