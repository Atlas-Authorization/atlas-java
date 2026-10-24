package net.atlasauth.atlas;

import java.util.List;

/** Names the Atlas API accepts in configuration and sends in webhooks. */
public final class AtlasConstants {

    private AtlasConstants() {
    }

    /**
     * Captcha providers ({@code auth_config.captcha.provider}). Captcha is bring-your-own-keys:
     * store the secret first. {@code vercel_botid} is secret-only: set {@code attestationUrl}
     * instead of a site key.
     */
    public static final List<String> CAPTCHA_PROVIDERS = List.of(
            "none", "atlas", "hcaptcha", "turnstile", "recaptcha", "recaptcha_v3", "recaptcha_enterprise",
            "funcaptcha", "friendly", "yandex", "geetest", "mcaptcha", "capy", "seon", "awswaf", "perimeterx",
            "akamai", "kasada", "incapsula", "captchafox", "vercel_botid");

    /** {@code auth_config.accountLinkingTrust} values. */
    public static final List<String> LINKING_TRUST_LEVELS = List.of("always", "verified", "never");

    /** Billing providers an instance can sell end-user plans through (bring your own key). */
    public static final List<String> BILLING_PROVIDERS =
            List.of("stripe", "polar", "dodo", "creem", "chargebee", "autumn", "commet");

    /** Where the token-signing key can live. */
    public static final List<String> SIGNING_KEY_PROVIDERS = List.of("local", "aws_kms", "gcp_kms", "azure_key_vault");

    /** A session's custom data changed. */
    public static final String WEBHOOK_EVENT_SESSION_UPDATED = "session.updated";

    /** A dormant account signed in again. */
    public static final String WEBHOOK_EVENT_USER_REACTIVATED = "user.reactivated";

    /** The security notice sent when a dormant account signs in again. */
    public static final String EMAIL_TEMPLATE_ACCOUNT_REACTIVATED = "account_reactivated";

    /** A user accepted a policy version (payload: the policy acceptance with its evidence). */
    public static final String WEBHOOK_EVENT_USER_POLICY_ACCEPTED = "user.policy_accepted";

    /** A policy version was published. */
    public static final String WEBHOOK_EVENT_POLICY_VERSION_PUBLISHED = "policy.version_published";

    /** A preflight finding code added with the security-posture and integration checks. */
    public static final String PREFLIGHT_OAUTH_CLIENT_NO_BACKCHANNEL_LOGOUT_URI = "oauth_client_no_backchannel_logout_uri";
    public static final String PREFLIGHT_PKCE_NOT_REQUIRED = "pkce_not_required";
    public static final String PREFLIGHT_PASSKEY_WITHOUT_UV = "passkey_without_uv";
    public static final String PREFLIGHT_AUTO_LINK_UNVERIFIED = "auto_link_unverified";
    public static final String PREFLIGHT_INTEGRATION_ENABLED_NEVER_MINTED = "integration_enabled_never_minted";
    public static final String PREFLIGHT_INTEGRATION_VERIFY_ERRORS = "integration_verify_errors";
    public static final String PREFLIGHT_INTEGRATION_RECEIVER_FAILING = "integration_receiver_failing";
    public static final String PREFLIGHT_COOKIE_CONSENT_NOT_ENABLED = "cookie_consent_not_enabled";
    public static final String PREFLIGHT_STATUS_DEPENDENCY_UNREACHABLE = "status_dependency_unreachable";
    public static final String PREFLIGHT_STATUS_DEPENDENCY_OUTAGE = "status_dependency_outage";

    /** Atlas service components (status subscription {@code component_ids}). */
    public static final List<String> STATUS_COMPONENTS =
            List.of("frontend_api", "backend_api", "database", "cache", "background_jobs");

    /** The referral program id of Referalli. The old spelling {@code referally} is still accepted by Atlas. */
    public static final String REFERRAL_PROVIDER_REFERALLI = "referalli";
    /** The former spelling of {@link #REFERRAL_PROVIDER_REFERALLI}; still accepted. */
    public static final String REFERRAL_PROVIDER_REFERALLY = "referally";

    /** Identity-integration presets. */
    public static final List<String> INTEGRATION_PRESETS = List.of("chatly", "forumulate", "cookiemunch", "referalli");

    /** Error codes an integration can report with {@code integrations().reportVerifyError}. */
    public static final List<String> INTEGRATION_VERIFY_ERRORS =
            List.of("aud_mismatch", "expired", "bad_signature", "unknown_kid", "session_revoked", "invalid_claims", "other");

    /** Canonical privacy request types (export, erasure and erase are accepted aliases). */
    public static final List<String> PRIVACY_REQUEST_TYPES =
            List.of("access", "deletion", "closure", "restriction", "rectification");

    /** Privacy request statuses. */
    public static final List<String> PRIVACY_REQUEST_STATUSES = List.of("pending", "scheduled", "processing",
            "completed", "completed_with_retention", "rejected", "no_match", "cancelled");

    /** Webhook event destination types. */
    public static final List<String> WEBHOOK_DESTINATION_TYPES =
            List.of("https", "aws_eventbridge", "gcp_pubsub", "cloudflare_queue");
}
