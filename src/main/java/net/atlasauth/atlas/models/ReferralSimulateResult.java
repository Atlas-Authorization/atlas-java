package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * {@code POST /v1/instance/referrals/simulate}: a sign-up call for a made-up user
 * ({@code atlas_test_<random>}). No user is created.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralSimulateResult(
        String object,
        boolean ok,
        /** The logged call, in the delivery log. */ String callId,
        String customerRef,
        Integer httpStatus,
        Integer latencyMs,
        /** The platform's answer, normalised; null when the call failed. */ Result result,
        /** The platform's raw answer, personal fields removed. */ Map<String, Object> response,
        ReferralConnectionTest.TestError error) {

    /** The platform's answer, normalised. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(Match referral, Partner partner, List<String> notes) {
    }

    /** The matched referral. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Match(String partnerId, String partnerExternalRef, String matchedVia, String status) {
    }

    /** The made-up user's own partner record, when the platform enrolled them. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Partner(String id, String code, String link) {
    }
}
