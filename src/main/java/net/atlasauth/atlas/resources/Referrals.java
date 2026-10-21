package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ReferralCall;
import net.atlasauth.atlas.models.ReferralConnectionTest;
import net.atlasauth.atlas.models.ReferralSettings;
import net.atlasauth.atlas.models.UpdateReferralSettingsBody;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/instance/referrals} — referral attribution: the instance's connection to a
 * referral platform, the connection test and the call log.
 *
 * <p>With referrals on, every interactive sign-up is reported to the platform before
 * {@code user.created} is sent; the answer lands on the user ({@code referral},
 * {@code referralCode}, {@code referralLink}). A late answer arrives as the
 * {@code user.referral_attributed} webhook. {@code signingSecret} and {@code apiKey} are
 * write-only.
 */
public final class Referrals {
    private final Transport t;

    public Referrals(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/instance/referrals} — settings, connection status and supported providers. */
    public ReferralSettings get() {
        return t.get("/v1/instance/referrals", new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/instance/referrals} — partial update; enabling without the provider's needs is 422 {@code REFERRALS_NOT_CONFIGURED}. */
    public ReferralSettings update(UpdateReferralSettingsBody body) {
        return update(body, null);
    }

    /** {@link #update(UpdateReferralSettingsBody)} with an {@code Idempotency-Key}. */
    public ReferralSettings update(UpdateReferralSettingsBody body, String idempotencyKey) {
        return t.request("PATCH", "/v1/instance/referrals", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/referrals/test} — a signed probe: ok on 2xx or 404; {@code signature_rejected} on 401/403. */
    public ReferralConnectionTest test() {
        return t.post("/v1/instance/referrals/test", null, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/referrals/calls} — the call log, newest first (default 50). */
    public ListPage<ReferralCall> listCalls() {
        return listCalls(null, null, null, null);
    }

    /**
     * {@code GET /v1/instance/referrals/calls} — the call log, newest first. Each filter may be
     * null: {@code limit} 1-200, {@code status} (pending, succeeded, retrying, failed, held,
     * skipped), {@code kind} (signup, erase, export, partner, signal, test), {@code userId}.
     */
    public ListPage<ReferralCall> listCalls(Integer limit, String status, String kind, String userId) {
        return t.get("/v1/instance/referrals/calls",
                Params.of().set("limit", limit).set("status", status).set("kind", kind).set("user_id", userId).map(),
                new TypeReference<>() {
                });
    }

    /** {@code POST /v1/instance/referrals/calls/:id/retry} — retry a failed, retrying or skipped call now. */
    public ReferralCall retryCall(String id) {
        return retryCall(id, null);
    }

    /** {@link #retryCall(String)} with an {@code Idempotency-Key}. */
    public ReferralCall retryCall(String id, String idempotencyKey) {
        return t.post("/v1/instance/referrals/calls/" + enc(id) + "/retry", null, idempotencyKey, new TypeReference<>() {
        });
    }
}
