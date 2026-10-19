package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** One Sign in with Atlas app's setup check, in the instance preflight's finding shape. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdAppPreflight(
        String object,
        String appId,
        boolean ok,
        long checkedAt,
        /** People who have approved the app. */ int userCount,
        List<PreflightFinding> findings) {
}
