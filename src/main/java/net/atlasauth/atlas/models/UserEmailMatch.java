package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserEmailMatch(
        String emailAddress,
        String userId,
        boolean verified,
        /** True only for a soft-deleted user matched with {@code includeDeleted}. */ Boolean deleted) {
    public UserEmailMatch(String emailAddress, String userId, boolean verified) {
        this(emailAddress, userId, verified, null);
    }
}
