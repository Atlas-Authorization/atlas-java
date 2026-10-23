package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code PATCH /v1/billing/plans/:id}. */
public final class UpdateBillingPlanBody {
    /** stripe (default), polar, dodo or creem. */
    public String provider;
    /** The plan price at the provider (the neutral alias of stripePriceId; send one). */
    public String providerPriceId;
    /** The yearly price at the provider. */
    public String annualPriceId;
    /** The yearly amount. */
    public String annualAmount;
    public String name;
    public String slug;
    public String stripePriceId;
    public String interval;
    public String amount;
    public String currency;
    public List<String> features;
    public String audience;
    public Boolean active;

    public UpdateBillingPlanBody name(String v) { this.name = v; return this; }
    public UpdateBillingPlanBody slug(String v) { this.slug = v; return this; }
    public UpdateBillingPlanBody stripePriceId(String v) { this.stripePriceId = v; return this; }
    public UpdateBillingPlanBody interval(String v) { this.interval = v; return this; }
    public UpdateBillingPlanBody amount(String v) { this.amount = v; return this; }
    public UpdateBillingPlanBody currency(String v) { this.currency = v; return this; }
    public UpdateBillingPlanBody features(List<String> v) { this.features = v; return this; }
    public UpdateBillingPlanBody audience(String v) { this.audience = v; return this; }
    public UpdateBillingPlanBody active(Boolean v) { this.active = v; return this; }

    public UpdateBillingPlanBody provider(String v) { this.provider = v; return this; }
    public UpdateBillingPlanBody providerPriceId(String v) { this.providerPriceId = v; return this; }
    public UpdateBillingPlanBody annualPriceId(String v) { this.annualPriceId = v; return this; }
    public UpdateBillingPlanBody annualAmount(String v) { this.annualAmount = v; return this; }
}
