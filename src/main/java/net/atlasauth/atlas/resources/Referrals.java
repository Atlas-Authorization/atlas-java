package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ReferralBackfillBody;
import net.atlasauth.atlas.models.ReferralBackfillJob;
import net.atlasauth.atlas.models.ReferralCall;
import net.atlasauth.atlas.models.ReferralConnectionTest;
import net.atlasauth.atlas.models.ReferralRetryFailedResult;
import net.atlasauth.atlas.models.ReferralSettings;
import net.atlasauth.atlas.models.ReferralSimulateResult;
import net.atlasauth.atlas.models.UpdateReferralSettingsBody;
import net.atlasauth.atlas.models.UserReferralResult;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/instance/referrals} — referral attribution: the instance's connection to a
 * referral platform, the connection test and the call log.
 *
 * <p>With referrals on, every interactive sign-up is reported to the platform before
 * {@code user.created} is sent; the answer lands on the user ({@code referral},
 * {@code referralCode}, {@code referralLink}). A late answer arrives as the
 * {@code user.referral_resolved} webhook, whose {@code data} is the full {@link net.atlasauth.atlas.models.User}
 * (and {@code user.referral_attributed} when it attributes the user). {@code provider} is
 * {@code webhook} (the Atlas referral protocol), {@code referalli} (the old {@code referally} is still accepted) or another built-in adapter.
 * {@code signingSecret} and {@code apiKey} are write-only.
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

    /**
     * {@code POST /v1/instance/referrals/test} — a ping where the provider answers one ({@code method}
     * ping, with {@code ping} and {@code warnings} such as clock skew), else a signed probe (ok on 2xx
     * or 404); {@code signature_rejected} on 401/403.
     */
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
     * null: {@code limit} 1-1000 (1-200 on servers before referral attribution v2), {@code status}
     * (pending, succeeded, retrying, failed, held, skipped), {@code kind} (signup, erase, export,
     * partner, signal, alias, user, ping, test), {@code userId}.
     */
    public ListPage<ReferralCall> listCalls(Integer limit, String status, String kind, String userId) {
        return listCalls(limit, status, kind, userId, null, null);
    }

    /**
     * {@link #listCalls(Integer, String, String, String)} with {@code errorCode} (only calls whose
     * last failure had this code, e.g. signature_rejected) and {@code startingAfter} (the id of the
     * last call of the previous page, while {@code hasMore}).
     */
    public ListPage<ReferralCall> listCalls(Integer limit, String status, String kind, String userId, String errorCode, String startingAfter) {
        return t.get("/v1/instance/referrals/calls",
                Params.of().set("limit", limit).set("status", status).set("kind", kind).set("user_id", userId)
                        .set("error_code", errorCode).set("starting_after", startingAfter).map(),
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

    /**
     * {@code POST /v1/instance/referrals/calls/retry_failed} — re-queue every failed call (not tests)
     * to send now. Each filter may be null: {@code kind}, {@code errorCode} (e.g.
     * signature_rejected after replacing the secret).
     */
    public ReferralRetryFailedResult retryFailed(String kind, String errorCode) {
        return retryFailed(kind, errorCode, null);
    }

    /** {@link #retryFailed(String, String)} with an {@code Idempotency-Key}. */
    public ReferralRetryFailedResult retryFailed(String kind, String errorCode, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (kind != null) {
            body.put("kind", kind);
        }
        if (errorCode != null) {
            body.put("error_code", errorCode);
        }
        return t.post("/v1/instance/referrals/calls/retry_failed", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/instance/referrals/simulate} — send a sign-up call for a made-up user
     * ({@code atlas_test_<random>}; no user is created) with an optional code (null for none), and
     * return what the platform answered.
     */
    public ReferralSimulateResult simulate(String code) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (code != null) {
            body.put("code", code);
        }
        return t.post("/v1/instance/referrals/simulate", body, new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/instance/referrals/backfill} — queue sign-up calls for existing users created at
     * or after {@code since}, with their real sign-up time. Users already reported are skipped.
     * Referrals must be on (422 {@code REFERRALS_NOT_CONFIGURED} otherwise). Poll
     * {@link #getBackfill(String)}.
     */
    public ReferralBackfillJob backfill(ReferralBackfillBody body) {
        return backfill(body, null);
    }

    /** {@link #backfill(ReferralBackfillBody)} with an {@code Idempotency-Key}. */
    public ReferralBackfillJob backfill(ReferralBackfillBody body, String idempotencyKey) {
        return t.post("/v1/instance/referrals/backfill", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/referrals/backfill/:id} — a backfill job and where its calls stand. */
    public ReferralBackfillJob getBackfill(String id) {
        return t.get("/v1/instance/referrals/backfill/" + enc(id), new TypeReference<>() {
        });
    }

    /**
     * {@code GET /v1/users/:id/referral} — the stored result for one user (who referred them, how,
     * their own code), with no call to the platform. 404 when the user was never reported.
     */
    public UserReferralResult getForUser(String userId) {
        return t.get("/v1/users/" + enc(userId) + "/referral", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/referrals/calls/{id}} — one call of the delivery log (or by the platform's request id). */
    public JsonNode getCall(String id) {
        return t.get("/v1/instance/referrals/calls/" + enc(id), new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/instance/referrals/{provider}/install} — One-click install: connect the instance to a
     * referral platform (for example {@code referalli}; the old spelling {@code referally} is accepted) and
     * get back the URL to finish the install in. {@code returnTo} (an https URL) is where the platform sends
     * the person afterwards; may be null.
     */
    public JsonNode install(String provider, String returnTo) {
        return t.post("/v1/instance/referrals/" + enc(provider) + "/install", Params.of("return_to", returnTo).map(), new TypeReference<>() {
        });
    }
}
