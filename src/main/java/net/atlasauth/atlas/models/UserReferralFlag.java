package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The result of {@code POST /v1/users/:id/referral/flag}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserReferralFlag(
        String object,
        String userId,
        /** {@code flagged} after the call. */ String status,
        boolean flagged,
        String flagReason,
        /** Epoch ms. */ Long flaggedAt,
        String riskLevel) {
}
