package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The {@code data} of a {@code user.referral_attributed} webhook: the referral platform
 * answered after {@code user.created} was sent.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserReferralAttributedEvent(
        String object,
        String userId,
        /** attributed, not_referred (no referrer found), failed, ... */ String attributionStatus,
        UserReferral referral,
        String referralCode,
        String referralLink,
        /** Epoch ms. */ long signedUpAt,
        long updatedAt) {
}
