package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A read view of who is subscribed. {@code status} is written ONLY by the verified
 * Stripe webhook, never through this SDK.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingSubscription(
        String object,
        String id,
        String subjectType,
        String subjectId,
        String planId,
        String status,
        String stripeSubscriptionId,
        String stripeCustomerId,
        Long currentPeriodEnd,
        long createdAt,
        long updatedAt) {
}
