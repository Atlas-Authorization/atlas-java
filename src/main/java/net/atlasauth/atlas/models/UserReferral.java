package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Where a user's inbound referral stands and, once attributed, who referred them. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserReferral(
        /** The platform's id for the referring partner. */ String partnerId,
        /** The referrer as the platform knows them (your external id for that user, or their Atlas id). */ String referrerExternalId,
        /** The referrer's Atlas user id, when they are a user of this instance. */ String referrerUserId,
        /** How the platform matched the referral, e.g. code, click, coupon. */ String matchedVia,
        /**
         * Where attribution stands: pending (the platform has not answered yet), attributed, none,
         * failed, held or flagged. Servers before referral attribution v2 sent the platform's own
         * status here; that is now {@link #referralStatus()}.
         */ String status,
        /** low, medium or high when Atlas assessed the sign-up, else null. */ String riskLevel,
        /** True once an admin marked the referral fraudulent. */ Boolean flagged,
        /** The referrer as the platform knows them (same value as referrerExternalId). */ String partnerExternalRef,
        /** The platform's own status for the referral, e.g. active. */ String referralStatus,
        /** The platform's reasoning notes (why it matched, or why not). */ List<String> notes) {

    /** The 1.7 shape, without partnerExternalRef, referralStatus and notes. */
    public UserReferral(String partnerId, String referrerExternalId, String referrerUserId, String matchedVia, String status, String riskLevel, Boolean flagged) {
        this(partnerId, referrerExternalId, referrerUserId, matchedVia, status, riskLevel, flagged, null, null, null);
    }
}
