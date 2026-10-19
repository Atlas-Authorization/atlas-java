package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Preflight(
        String object,
        boolean ok,
        long checkedAt,
        List<PreflightFinding> findings,
        /** Atlas browser SDK versions seen in the last 7 days, with their advisories. */ List<SdkVersionSeen> sdkVersionsSeen) {

    /** The previous shape, without {@code sdkVersionsSeen}. */
    public Preflight(String object, boolean ok, long checkedAt, List<PreflightFinding> findings) {
        this(object, ok, checkedAt, findings, null);
    }
}
