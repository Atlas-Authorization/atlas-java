package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A page origin the Sign in with Atlas button script (or the no-script fallback link) was seen on. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdOriginSeen(String origin, long lastSeenAt, int requests) {
}
