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
        long updatedAt,
        /** The billing provider the plan sells through: stripe, polar, dodo or creem. */ String provider,
        /** The plan price at the provider (the same value as stripePriceId). */ String providerPriceId,
        /** The yearly price at the provider, or null. */ String annualPriceId,
        /** The yearly amount, or null. */ String annualAmount) {

    /** The 1.10 shape, before the 1.11 fields. */
    public BillingPlan(String object, String id, String name, String slug, String stripePriceId, String interval, String amount, String currency, List<String> features, String audience, boolean active, long createdAt, long updatedAt) {
        this(object, id, name, slug, stripePriceId, interval, amount, currency, features, audience, active, createdAt, updatedAt, null, null, null, null);
    }
}
