package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * {@code GET /v1/instance/referrals}: the instance's referral platform connection. Secrets are
 * never returned, only {@code signingSecretSet} / {@code apiKeySet}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralSettings(
        String object,
        boolean enabled,
        /** webhook (the Atlas referral protocol) or a built-in vendor adapter id. */ String provider,
        String endpointUrl,
        String programId,
        String connectionId,
        boolean signingSecretSet,
        boolean apiKeySet,
        /** Query parameters that carry a referral code, in priority order. */ List<String> urlParams,
        String contextCookie,
        String consentCategory,
        String consentFallbackCategory,
        boolean autoEnrollUsers,
        /** e.g. https://app.example.com/join?ref={code}; builds referral_link. */ String linkTemplate,
        Integer timeoutMs,
        ReferralSecuritySettings security,
        /** What the chosen provider still needs before referrals can be enabled. */ List<String> missing,
        Connection connection,
        List<Provider> providers,
        Long updatedAt) {

    /** The connection's health. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Connection(
            LastTest lastTest,
            /** Calls that failed permanently in the last 7 days. */ @JsonProperty("failed_calls_7d") int failedCalls7d,
            /** Calls waiting for a retry. */ int retryingCalls,
            Long lastSuccessAt) {
    }

    /** The last connection test. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LastTest(boolean ok, /** Epoch ms. */ long at, Integer httpStatus, String error) {
    }

    /** A referral provider Atlas supports. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Provider(
            String id,
            String name,
            Capabilities capabilities,
            /** endpoint_url, signing_secret, api_key, connection_id. */ List<String> needs,
            String defaultContextCookie,
            String docsUrl) {
    }

    /** What a provider can do; an operation it lacks is recorded as skipped. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Capabilities(boolean signup, boolean erase, boolean export, boolean partner, boolean autoEnroll, boolean signal) {
    }
}
