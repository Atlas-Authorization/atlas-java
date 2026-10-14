package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BillingPlan;
import net.atlasauth.atlas.models.BillingSeatSync;
import net.atlasauth.atlas.models.BillingSubscription;
import net.atlasauth.atlas.models.BillingUsageRecorded;
import net.atlasauth.atlas.models.CreateBillingPlanBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ImportSubscriptionBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ReportBillingUsageBody;
import net.atlasauth.atlas.models.UpdateBillingPlanBody;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/billing} resource — plans and the subscription read view. */
public final class Billing {

    private final Transport t;

    public Billing(Transport t) {
        this.t = t;
    }

    public ListPage<BillingPlan> listPlans() {
        return t.get("/v1/billing/plans", new TypeReference<>() {
        });
    }

    public BillingPlan createPlan(CreateBillingPlanBody body) {
        return createPlan(body, null);
    }

    public BillingPlan createPlan(CreateBillingPlanBody body, String idempotencyKey) {
        return t.post("/v1/billing/plans", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public BillingPlan getPlan(String id) {
        return t.get("/v1/billing/plans/" + enc(id), new TypeReference<>() {
        });
    }

    public BillingPlan updatePlan(String id, UpdateBillingPlanBody body) {
        return t.patch("/v1/billing/plans/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject deletePlan(String id) {
        return t.delete("/v1/billing/plans/" + enc(id), new TypeReference<>() {
        });
    }

    public ListPage<BillingSubscription> listSubscriptions() {
        return listSubscriptions(null, null);
    }

    /** {@code GET /v1/billing/subscriptions} — the read view, optionally scoped to one subject. */
    public ListPage<BillingSubscription> listSubscriptions(String subjectType, String subjectId) {
        Map<String, Object> q = Params.of()
                .set("subject_type", subjectType)
                .set("subject_id", subjectId)
                .map();
        return t.get("/v1/billing/subscriptions", q, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/billing/subscriptions/import} — Adopt an EXISTING Stripe subscription (same Stripe account) onto an Atlas user/org + plan, for migrating billing into Atlas without re-charging. */
    public BillingSubscription importSubscription(ImportSubscriptionBody body) {
        return t.post("/v1/billing/subscriptions/import", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public BillingSubscription importSubscription(ImportSubscriptionBody body, String idempotencyKey) {
        return t.post("/v1/billing/subscriptions/import", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/billing/usage} — Report metered usage for a subject (idempotent on idempotency_key); mirrored to a Stripe meter event when the plan is metered and configured. */
    public BillingUsageRecorded reportUsage(ReportBillingUsageBody body) {
        return t.post("/v1/billing/usage", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public BillingUsageRecorded reportUsage(ReportBillingUsageBody body, String idempotencyKey) {
        return t.post("/v1/billing/usage", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/billing/subjects/{type}/{id}/sync_seats} — Sync a per-seat subscription's Stripe quantity to the org's current member count (proration applied). */
    public BillingSeatSync syncSeats(String type, String id) {
        return t.post("/v1/billing/subjects/" + enc(type) + "/" + enc(id) + "/sync_seats", null, new TypeReference<>() {
        });
    }
}
