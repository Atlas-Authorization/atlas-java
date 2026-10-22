package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A referral backfill job. Users already reported are skipped. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralBackfillJob(
        String object,
        String id,
        /** running while any queued call is still to be sent, then completed. */ String status,
        /** Epoch ms. */ long since,
        String codeMetadataPath,
        /** How many codes were given in {@code codes}. */ Integer codesGiven,
        int usersScanned,
        /** Already reported, or no code while auto-enrol is off. */ int usersSkipped,
        int callsQueued,
        /** More users matched than one backfill takes: run again from nextSince. */ boolean truncated,
        /** Epoch ms, when truncated. */ Long nextSince,
        Calls calls,
        long createdAt,
        Long finishedAt) {

    /** Where the queued calls stand. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Calls(int pending, int succeeded, int failed, int skipped, int held) {
    }
}
