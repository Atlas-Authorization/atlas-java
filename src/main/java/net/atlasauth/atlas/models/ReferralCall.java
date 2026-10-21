package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** One call in the referral call log. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralCall(
        String object,
        /** Also the signed atlas-id header of every attempt. */ String id,
        /** signup, erase, export, partner, signal or test. */ String kind,
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
        Long updatedAt) {
}
