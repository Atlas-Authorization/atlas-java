package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A per-key verify throttle: at most {@code max} verifies per {@code windowSeconds}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyRateLimit(long max, long windowSeconds) {
}
