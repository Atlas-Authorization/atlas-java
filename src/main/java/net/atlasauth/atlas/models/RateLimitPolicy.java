package net.atlasauth.atlas.models;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
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
        RateLimitPolicyBounds bounds,
        Integer fapiVerifyPerMin,
        Integer ldapPerIpPerMin,
        Integer ldapPerUserPerMin,
        /** Per-endpoint rules (one per group) over the built-in limiter. */ List<RateLimitRule> rules,
        /** The groups a rule may name, with what each covers and its budget. */ JsonNode groups) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public RateLimitPolicy(String object, int fapiCreatePerMin, int bapiPerMin, RateLimitPolicyBounds bounds) {
        this(object, fapiCreatePerMin, bapiPerMin, bounds, null, null, null, null, null);
    }
}
