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
        List<LoginReadinessChecks> checks) {
}
