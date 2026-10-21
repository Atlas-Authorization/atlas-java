package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailAddress(
        String object,
        String id,
        String emailAddress,
        boolean verified,
        boolean primary,
        long createdAt,
        /** Whether and how the address was proven (null from older servers). */ EmailAddressVerification verification) {
    /** The 1.4 shape, without {@code verification}. */
    public EmailAddress(String object, String id, String emailAddress, boolean verified, boolean primary, long createdAt) {
        this(object, id, emailAddress, verified, primary, createdAt, null);
    }
}
