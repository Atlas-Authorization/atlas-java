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
}
