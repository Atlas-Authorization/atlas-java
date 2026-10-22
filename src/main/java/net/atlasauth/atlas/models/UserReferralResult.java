package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * {@code GET /v1/users/:id/referral}: the stored result of a user's referral calls, with no call
 * to the platform. 404 when the user was never reported.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserReferralResult(
        String object,
        String userId,
        /** pending, attributed, none, failed, held or flagged. */ String status,
        /** The id the platform knows the user by: their external_id, else the Atlas user id. */ String customerRef,
        String partnerId,
        /** The referrer as the platform knows them. */ String partnerExternalRef,
        String referrerUserId,
        String matchedVia,
        /** The platform's own status for the referral. */ String referralStatus,
        /** The platform's reasoning notes. */ List<String> notes,
        String referralCode,
        String referralLink,
        /** The referral captured at sign-up, as sent (camelCase). */ Map<String, Object> context,
        String riskLevel,
        boolean flagged,
        /** Why the sign-up report is held, while held. */ String holdReason,
        /** Epoch ms. */ long signedUpAt,
        long updatedAt) {
}
