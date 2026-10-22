package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** What a referral platform answered to a ping (the connection test, where supported). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralPing(
        boolean ok,
        /** Epoch ms. */ Long at,
        Integer httpStatus,
        /** e.g. signature_rejected, connection_paused, timeout, network_error, ping_not_ok. */ String errorCode,
        String error,
        Integer latencyMs,
        String connectionId,
        String programId,
        String programName,
        String orgName,
        /** The platform connection's own auto-enrol setting. */ Boolean autoEnrollUsers,
        /** The platform's clock, as it reported it. */ String serverTime,
        /** The platform's clock minus Atlas's, in ms. Signed calls fail past 5 minutes. */ Long clockSkewMs,
        /** True when the skew is over 60 seconds. */ Boolean clockSkewWarning) {
}
