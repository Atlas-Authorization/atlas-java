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
        long updatedAt,
        /** The billing provider the subscription lives on. */ String provider,
        /** The subscription id at the provider. */ String providerSubscriptionId,
        /** The customer id at the provider. */ String providerCustomerId,
        /** "instance" (your own provider account) or "platform" (a legacy shared-account subscription: mirrored, not manageable). */ String account,
        /** month or year. */ String interval,
        /** True while a cancellation is scheduled for the period end. */ boolean cancelAtPeriodEnd,
        /** A plan change scheduled for the period end, or null. */ String scheduledPlanId,
        /** The interval of the scheduled change, or null. */ String scheduledInterval,
        /** When the scheduled change applies (epoch ms), or null. */ Long scheduledChangeAt,
        /** When the trial ends (epoch ms), or null. */ Long trialEnd,
        /** When the subscription was canceled (epoch ms), or null. */ Long canceledAt,
        /** On a change response: true when the switch waits for the period end. */ boolean scheduled,
        /** What an entitlement provider (Autumn, Commet) reports this subscription grants; null for other providers. */ BillingEntitlements entitlements) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public BillingSubscription(String object, String id, String subjectType, String subjectId, String planId, String status, String stripeSubscriptionId, String stripeCustomerId, Long currentPeriodEnd, long createdAt, long updatedAt, String provider, String providerSubscriptionId, String providerCustomerId, String account, String interval, boolean cancelAtPeriodEnd, String scheduledPlanId, String scheduledInterval, Long scheduledChangeAt, Long trialEnd, Long canceledAt, boolean scheduled) {
        this(object, id, subjectType, subjectId, planId, status, stripeSubscriptionId, stripeCustomerId, currentPeriodEnd, createdAt, updatedAt, provider, providerSubscriptionId, providerCustomerId, account, interval, cancelAtPeriodEnd, scheduledPlanId, scheduledInterval, scheduledChangeAt, trialEnd, canceledAt, scheduled, null);
    }

    /** The 1.10 shape, before the 1.11 fields. */
    public BillingSubscription(String object, String id, String subjectType, String subjectId, String planId, String status, String stripeSubscriptionId, String stripeCustomerId, Long currentPeriodEnd, long createdAt, long updatedAt) {
        this(object, id, subjectType, subjectId, planId, status, stripeSubscriptionId, stripeCustomerId, currentPeriodEnd, createdAt, updatedAt, null, null, null, null, null, false, null, null, null, null, null, false);
    }
}
