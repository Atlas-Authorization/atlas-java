package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The floor/ceiling each rate-limit knob is bounded to when written. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RateLimitPolicyBounds(
        RateLimitBound fapiCreatePerMin,
        RateLimitBound bapiPerMin) {
}
