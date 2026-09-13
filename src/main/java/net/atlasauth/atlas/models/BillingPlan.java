package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A plan a tenant defines for their app's users. {@code stripePriceId} is the
 * recurring price a subscriber is charged; {@code features} are the keys the plan
 * grants, surfaced to the end user as the {@code fea} session claim.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingPlan(
        String object,
        String id,
        String name,
        String slug,
        String stripePriceId,
        String interval,
        String amount,
        String currency,
        List<String> features,
        String audience,
        boolean active,
        long createdAt,
        long updatedAt) {
}
