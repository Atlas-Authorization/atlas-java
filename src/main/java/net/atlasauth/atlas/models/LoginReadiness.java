package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginReadiness(
        String object,
        String origin,
        boolean live,
        String dataRegion,
        boolean ready,
        List<LoginReadinessChecks> checks,
        /** The integration the checks assumed for the origin, and why (null from older servers). */ LoginReadinessIntegration integration) {
    /** The 1.4 shape, without {@code integration}. */
    public LoginReadiness(String object, String origin, boolean live, String dataRegion, boolean ready, List<LoginReadinessChecks> checks) {
        this(object, origin, live, dataRegion, ready, checks, null);
    }
}
