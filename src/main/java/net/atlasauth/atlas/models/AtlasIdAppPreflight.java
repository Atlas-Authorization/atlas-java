package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** One Sign in with Atlas app's setup check, in the instance preflight's finding shape. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdAppPreflight(
        String object,
        String appId,
        boolean ok,
        long checkedAt,
        /** People who have approved the app. */ int userCount,
        List<PreflightFinding> findings,
        /** The page origins the button script ran on in the last 7 days. */ List<AtlasIdOriginSeen> buttonRenderedOrigins,
        /** How the last 7 days' sign-ins started. */ @JsonProperty("authorize_requests_7d") AtlasIdAuthorizeSources authorizeRequests7d,
        /** Origins of sign-ins that came through the no-script fallback link. */ List<AtlasIdOriginSeen> fallbackOrigins,
        /** Whether the app's homepage references the button script (null = could not check). */ Boolean homepageIncludesScript) {
    /** The shape before buttonRenderedOrigins, authorizeRequests7d, fallbackOrigins, homepageIncludesScript. */
    public AtlasIdAppPreflight(String object, String appId, boolean ok, long checkedAt, int userCount, List<PreflightFinding> findings) {
        this(object, appId, ok, checkedAt, userCount, findings, null, null, null, null);
    }

}
