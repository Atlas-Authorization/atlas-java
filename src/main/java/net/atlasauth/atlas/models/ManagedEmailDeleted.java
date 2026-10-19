package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Acknowledges removing managed email. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedEmailDeleted(
        String object,
        String domain,
        boolean deleted) {
}
