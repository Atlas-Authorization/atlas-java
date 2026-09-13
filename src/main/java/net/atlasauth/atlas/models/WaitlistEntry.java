package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A waitlist entry from {@code /v1/waitlist_entries}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WaitlistEntry(
        String object,
        String id,
        String emailAddress,
        String status,
        String note,
        String decidedBy,
        Long decidedAt,
        long createdAt) {
}
