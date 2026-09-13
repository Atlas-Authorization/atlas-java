package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One email address as serialised inside an export job's user record. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportExportEmailAddress(
        String id,
        String emailAddress,
        boolean verified,
        boolean primary) {
}
