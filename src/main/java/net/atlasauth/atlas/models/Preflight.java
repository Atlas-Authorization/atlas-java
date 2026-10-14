package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Preflight(
        String object,
        boolean ok,
        long checkedAt,
        List<PreflightFinding> findings) {
}
