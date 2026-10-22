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
        /** webhook (the Atlas referral protocol), referally or another built-in vendor adapter id. */ String provider,
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
        Long updatedAt,
        /** Public tracking token (the referally provider derives the code lookup from it). Not a secret. */ String trackingToken,
        /** Public, CORS-enabled URL template with {code} for "X invited you" on hosted sign-up. */ String codeLookupUrl,
        /** Show an optional "Referral code" field on hosted sign-up. */ Boolean signupCodeField,
        /** Forward email, name and suspension changes to the platform (default true). */ Boolean forwardLifecycle,
        /** Mirror the result into public_metadata.referral (default false). */ Boolean mirrorToPublicMetadata,
        /** Add the user's own code as a referral_code claim to session and ID tokens (default false). */ Boolean includeReferralClaims) {

    /** The 1.7 shape, without the referral attribution v2 fields. */
    public ReferralSettings(String object, boolean enabled, String provider, String endpointUrl, String programId, String connectionId, boolean signingSecretSet, boolean apiKeySet, List<String> urlParams, String contextCookie, String consentCategory, String consentFallbackCategory, boolean autoEnrollUsers, String linkTemplate, Integer timeoutMs, ReferralSecuritySettings security, List<String> missing, Connection connection, List<Provider> providers, Long updatedAt) {
        this(object, enabled, provider, endpointUrl, programId, connectionId, signingSecretSet, apiKeySet, urlParams, contextCookie, consentCategory, consentFallbackCategory, autoEnrollUsers, linkTemplate, timeoutMs, security, missing, connection, providers, updatedAt, null, null, null, null, null, null);
    }

    /** The connection's health. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Connection(
            LastTest lastTest,
            /** Calls that failed permanently in the last 7 days. */ @JsonProperty("failed_calls_7d") int failedCalls7d,
            /** Calls waiting for a retry. */ int retryingCalls,
            Long lastSuccessAt,
            /** The last ping, when the provider answers one. */ ReferralPing lastPing,
            /** Set while delivery is blocked on something only a person can fix. */ Alert alert) {

        /** The 1.7 shape, without lastPing and alert. */
        public Connection(LastTest lastTest, int failedCalls7d, int retryingCalls, Long lastSuccessAt) {
            this(lastTest, failedCalls7d, retryingCalls, lastSuccessAt, null, null);
        }
    }

    /** Delivery is blocked on something only a person can fix. Show it as a banner. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Alert(
            /** signature_rejected (the platform answered 401: a rotated secret or clock skew) or connection_paused. */ String code,
            String message,
            /** Epoch ms. */ long at,
            String callId) {
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
            /** endpoint_url, signing_secret, api_key, connection_id, tracking_token. */ List<String> needs,
            String defaultContextCookie,
            String docsUrl) {
    }

    /** What a provider can do; an operation it lacks is recorded as skipped. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Capabilities(
            boolean signup,
            boolean erase,
            boolean export,
            boolean partner,
            boolean autoEnroll,
            boolean signal,
            /** Re-keys a customer whose external_id changed after sign-up. */ Boolean alias,
            /** Takes user lifecycle updates (email, name, suspension). */ Boolean user,
            /** Answers a ping for the connection test (org, program, clock). */ Boolean ping) {

        /** The 1.7 shape, without alias, user and ping. */
        public Capabilities(boolean signup, boolean erase, boolean export, boolean partner, boolean autoEnroll, boolean signal) {
            this(signup, erase, export, partner, autoEnroll, signal, null, null, null);
        }
    }
}
