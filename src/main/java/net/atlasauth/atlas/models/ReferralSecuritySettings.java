package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Fraud and bot protection for referred sign-ups ({@code security} on the referral settings). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralSecuritySettings(
        /** always (default): a sign-up carrying a referral code must pass the bot challenge; or inherit. */ String challengeReferredSignups,
        /** flag (default), strip or hold. */ String onHighRisk,
        /** Hold every referred sign-up until the email is proven. */ boolean holdUntilEmailVerified,
        /** Referred sign-ups per code from one IP network or device per hour before a velocity signal (1-1000, default 5). */ int maxReferredSignupsPerIpHour) {
}
