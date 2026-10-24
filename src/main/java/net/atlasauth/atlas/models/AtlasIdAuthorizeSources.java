package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** How the last 7 days' sign-ins started: the rendered button, the script API, One Tap, the no-script fallback, or unmarked. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdAuthorizeSources(int button, int js, int fedcm, int fallback, int unmarked) {
}
