package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Per-instance rate-limit policy — tunable budgets over the built-in request
 * limiter. Both knobs default to the built-in fixed values, so an unset policy
 * changes nothing.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RateLimitPolicy(
        String object,
        int fapiCreatePerMin,
        int bapiPerMin,
        RateLimitPolicyBounds bounds) {
}
