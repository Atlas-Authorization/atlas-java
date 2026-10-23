package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.databind.JsonNode;
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

    /**
     * {@code POST /v1/billing/subscriptions/{id}/change} — switch to another plan or interval on the
     * billing provider, now (prorated) or at the period end ({@code scheduleAtPeriodEnd}).
     */
    public BillingSubscription changeSubscription(String id, String planId, String interval, Boolean scheduleAtPeriodEnd) {
        Map<String, Object> body = Params.of().set("plan_id", planId).set("interval", interval)
                .set("schedule_at_period_end", scheduleAtPeriodEnd).map();
        return t.post("/v1/billing/subscriptions/" + enc(id) + "/change", body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/billing/subscriptions/{id}/cancel} — at the period end, or now when {@code immediately}. */
    public BillingSubscription cancelSubscription(String id, boolean immediately) {
        Map<String, Object> body = immediately ? Params.of("immediately", true).map() : Params.of().map();
        return t.post("/v1/billing/subscriptions/" + enc(id) + "/cancel", body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/billing/subscriptions/{id}/restore} — undo a cancellation scheduled for the period end. */
    public BillingSubscription restoreSubscription(String id) {
        return t.post("/v1/billing/subscriptions/" + enc(id) + "/restore", null, new TypeReference<>() {
        });
    }

    /**
     * {@code GET /v1/billing/providers} — the providers (stripe, polar, dodo, creem) this instance has
     * its OWN credentials for. Secrets are never returned ({@code secrets_set} only); each carries the
     * {@code webhook_url} to register with the provider.
     */
    public JsonNode listProviders() {
        return t.get("/v1/billing/providers", new TypeReference<>() {
        });
    }

    public JsonNode getProvider(String provider) {
        return t.get("/v1/billing/providers/" + enc(provider), new TypeReference<>() {
        });
    }

    /**
     * {@code PUT /v1/billing/providers/{provider}} — store credentials (merged onto what is stored).
     * {@code config} keys: Stripe {@code secret_key}, {@code webhook_secret}, {@code publishable_key};
     * Polar {@code access_token}, {@code webhook_secret}; Dodo and Creem {@code api_key},
     * {@code webhook_secret}. {@code environment} is "live" or "test".
     */
    public JsonNode setProvider(String provider, String environment, Map<String, String> config) {
        Map<String, Object> body = Params.of().set("environment", environment).set("config", config).map();
        return t.put("/v1/billing/providers/" + enc(provider), body, new TypeReference<>() {
        });
    }

    /** One read-only call with the stored credentials: {@code { ok, error, account }}. */
    public JsonNode testProvider(String provider) {
        return t.post("/v1/billing/providers/" + enc(provider) + "/test", null, new TypeReference<>() {
        });
    }

    /** Remove a provider's credentials; refused while subscriptions or plans depend on it unless {@code force}. */
    public JsonNode deleteProvider(String provider, boolean force) {
        Map<String, Object> q = force ? Params.of("force", "true").map() : Params.of().map();
        return t.delete("/v1/billing/providers/" + enc(provider), q, new TypeReference<>() {
        });
    }
}
