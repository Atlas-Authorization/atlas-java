package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The inbound referral the referral platform matched for a user. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserReferral(
        /** The platform's id for the referring partner. */ String partnerId,
        /** The referrer as the platform knows them (your external id for that user, or their Atlas id). */ String referrerExternalId,
        /** The referrer's Atlas user id, when they are a user of this instance. */ String referrerUserId,
        /** How the platform matched the referral, e.g. code, click, coupon. */ String matchedVia,
        /** The platform's own status for the referral. */ String status,
        /** low, medium or high when Atlas assessed the sign-up, else null. */ String riskLevel,
        /** True once an admin marked the referral fraudulent. */ Boolean flagged) {
}
