package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/billing/plans}. */
public final class CreateBillingPlanBody {
    public String name;
    public String slug;
    public String stripePriceId;
    /** One of {@code month}, {@code year}. Defaults to {@code month}. */
    public String interval;
    public String amount;
    public String currency;
    public List<String> features;
    /** One of {@code user}, {@code org}. Defaults to {@code user}. */
    public String audience;
    public Boolean active;

    public CreateBillingPlanBody() {
    }

    public CreateBillingPlanBody(String name, String slug, String stripePriceId) {
        this.name = name;
        this.slug = slug;
        this.stripePriceId = stripePriceId;
    }

    public CreateBillingPlanBody name(String v) { this.name = v; return this; }
    public CreateBillingPlanBody slug(String v) { this.slug = v; return this; }
    public CreateBillingPlanBody stripePriceId(String v) { this.stripePriceId = v; return this; }
    public CreateBillingPlanBody interval(String v) { this.interval = v; return this; }
    public CreateBillingPlanBody amount(String v) { this.amount = v; return this; }
    public CreateBillingPlanBody currency(String v) { this.currency = v; return this; }
    public CreateBillingPlanBody features(List<String> v) { this.features = v; return this; }
    public CreateBillingPlanBody audience(String v) { this.audience = v; return this; }
    public CreateBillingPlanBody active(Boolean v) { this.active = v; return this; }
}
