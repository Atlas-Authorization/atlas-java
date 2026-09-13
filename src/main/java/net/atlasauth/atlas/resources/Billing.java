package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BillingPlan;
import net.atlasauth.atlas.models.BillingSubscription;
import net.atlasauth.atlas.models.CreateBillingPlanBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
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
}
