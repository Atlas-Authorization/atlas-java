package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Acknowledges removing managed email. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedEmailDeleted(
        String object,
        String domain,
        boolean deleted,
        /** Whether the email provider that held the key (mode managed_domain) was removed too. */ Boolean providerRemoved) {
    /** The 1.4 shape, without {@code providerRemoved}. */
    public ManagedEmailDeleted(String object, String domain, boolean deleted) {
        this(object, domain, deleted, null);
    }
}
