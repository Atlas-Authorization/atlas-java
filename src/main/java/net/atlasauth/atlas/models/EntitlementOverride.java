package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** A sales-led entitlement override (P1-6): a non-Stripe grant pinning a plan / features / limits onto a user or org. A non-expired override wins over the Stripe-derived entitlement. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EntitlementOverride(
        String object,
        String id,
        String subjectType,
        String subjectId,
        String plan,
        List<String> features,
        Map<String, Object> limits,
        Long expiresAt,
        String createdBy,
        long createdAt) {
}
