package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The min/max a single rate-limit knob is clamped to on write. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RateLimitBound(
        int min,
        int max) {
}
