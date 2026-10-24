package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Preflight(
        String object,
        boolean ok,
        long checkedAt,
        List<PreflightFinding> findings,
        /** Atlas browser SDK versions seen in the last 7 days, with their advisories. */ List<SdkVersionSeen> sdkVersionsSeen,
        /** The {@code min_severity} the findings were filtered by. */ String minSeverity,
        /** Findings that do not apply to how the instance integrates (not counted in {@code ok}). */ List<JsonNode> suppressed,
        /** How each allowed https origin signs in ({@code origin}, {@code integration}, {@code source}, {@code confident}, {@code reason}). */ List<JsonNode> integrations) {
    /** The shape before {@code minSeverity}, {@code suppressed}, {@code integrations}. */
    public Preflight(String object, boolean ok, long checkedAt, List<PreflightFinding> findings, List<SdkVersionSeen> sdkVersionsSeen) {
        this(object, ok, checkedAt, findings, sdkVersionsSeen, null, null, null);
    }


    /** The previous shape, without {@code sdkVersionsSeen}. */
    public Preflight(String object, boolean ok, long checkedAt, List<PreflightFinding> findings) {
        this(object, ok, checkedAt, findings, null);
    }
}
