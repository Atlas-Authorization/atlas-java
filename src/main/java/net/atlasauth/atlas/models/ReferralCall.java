package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** One call in the referral call log. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralCall(
        String object,
        /** Also the signed atlas-id header of every attempt. */ String id,
        /** signup, erase, export, partner, signal, alias, user, ping or test. */ String kind,
        /** pending, succeeded, retrying, failed, held or skipped. */ String status,
        /** Sign-ups: referred, not_referred or stripped. */ String outcome,
        String customerRef,
        String userId,
        Integer httpStatus,
        String lastError,
        int attempts,
        /** Epoch ms of the next retry, while pending or retrying. */ Long nextAttemptAt,
        ReferralRisk risk,
        /** What was sent (never secrets). */ Map<String, Object> payload,
        long createdAt,
        Long updatedAt,
        /** Round trip of the last attempt, in ms. */ Integer latencyMs,
        /**
         * Why the last attempt failed, e.g. signature_rejected (401, not retried), connection_paused
         * (not retried), rate_limited, timeout, network_error, alias_conflict, http_&lt;status&gt;.
         */ String errorCode,
        /** The platform's last answer, personal fields removed. */ Map<String, Object> response,
        /** Sign-up calls: how the platform matched the referral and why. */ Highlights highlights,
        /** The platform's request id for its last answer. */ String requestId,
        /** The backfill job that queued the call. */ String batchId) {

    /** The 1.7 shape, without the referral attribution v2 fields. */
    public ReferralCall(String object, String id, String kind, String status, String outcome, String customerRef, String userId, Integer httpStatus, String lastError, int attempts, Long nextAttemptAt, ReferralRisk risk, Map<String, Object> payload, long createdAt, Long updatedAt) {
        this(object, id, kind, status, outcome, customerRef, userId, httpStatus, lastError, attempts, nextAttemptAt, risk, payload, createdAt, updatedAt, null, null, null, null, null, null);
    }

    /** How the platform matched a sign-up's referral, and its notes. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Highlights(String matchedVia, String partnerExternalRef, List<String> notes) {
    }
}
