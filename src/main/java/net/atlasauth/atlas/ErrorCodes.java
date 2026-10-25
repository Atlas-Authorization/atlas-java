package net.atlasauth.atlas;

/**
 * Stable error codes the API returns in {@link ErrorItem#code()}. Compare against
 * {@link AtlasApiException#code()} or use {@link AtlasApiException#hasCode(String)}.
 * Not exhaustive: the server may add codes; branch on the ones you handle.
 */
public final class ErrorCodes {

    private ErrorCodes() {
    }

    /** The request failed validation; {@code param} names the field. */
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    /** A strict-mode write sent a body field the endpoint does not accept. */
    public static final String UNKNOWN_FIELD = "UNKNOWN_FIELD";
    /** The body set a field that is not writable on this endpoint ({@code param} names it). */
    public static final String READ_ONLY_FIELD = "READ_ONLY_FIELD";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String RATE_LIMITED = "RATE_LIMITED";
    /** An Idempotency-Key was reused with different parameters. */
    public static final String IDEMPOTENCY_CONFLICT = "IDEMPOTENCY_CONFLICT";
    public static final String NOT_CONFIGURED = "NOT_CONFIGURED";
    public static final String UNAUTHENTICATED = "UNAUTHENTICATED";
    /** The key is unknown, revoked or malformed. */
    public static final String INVALID_KEY = "INVALID_KEY";
    /** The key is valid but for a different surface (e.g. a platform key on an instance route). */
    public static final String WRONG_KEY_SURFACE = "WRONG_KEY_SURFACE";
    public static final String FORBIDDEN = "FORBIDDEN";
    /**
     * The key lacks the scope this route needs ({@code meta.required_scope} names it; 403). It replaces
     * {@link #SCOPE_MISSING}; use {@link AtlasApiException#isInsufficientScope()}, which accepts both.
     */
    public static final String INSUFFICIENT_SCOPE = "INSUFFICIENT_SCOPE";
    /** The former spelling of {@link #INSUFFICIENT_SCOPE} (the new envelope carries it as {@code meta.legacy_code}); still tolerated. */
    public static final String SCOPE_MISSING = "SCOPE_MISSING";
    /** A root action (create an app or instance, mint a key) needs a multi-factor sign-in within the last five minutes (403). */
    public static final String STEP_UP_REQUIRED = "STEP_UP_REQUIRED";
    /** No endpoint matches the method and path (a typo or a guessed route); see {@link ErrorItem#didYouMean()} and {@link ErrorItem#allowedMethods()}. */
    public static final String ROUTE_NOT_FOUND = "ROUTE_NOT_FOUND";
    /** The call is for a live instance only. */
    public static final String NOT_A_LIVE_INSTANCE = "NOT_A_LIVE_INSTANCE";
    /** The call is refused on anything but a test-mode instance. */
    public static final String TEST_MODE_ONLY = "TEST_MODE_ONLY";
    /** Testing tokens are not available on this deployment. */
    public static final String TESTING_UNAVAILABLE = "TESTING_UNAVAILABLE";
    /** The partner-link credential an integration needs is not available on this instance. */
    public static final String PARTNER_LINK_UNAVAILABLE = "PARTNER_LINK_UNAVAILABLE";
    /** The key's origin allowlist refuses the request's Origin (403). */
    public static final String ORIGIN_NOT_ALLOWED = "ORIGIN_NOT_ALLOWED";
    /** Email sending is not set up for this instance. */
    public static final String EMAIL_DELIVERY_NOT_CONFIGURED = "EMAIL_DELIVERY_NOT_CONFIGURED";
    /** The operation needs an email provider to be configured first. */
    public static final String EMAIL_PROVIDER_REQUIRED = "EMAIL_PROVIDER_REQUIRED";
    /**
     * The email provider cannot be removed while email-dependent features are on and
     * nothing else would deliver email ({@code meta.features} lists them); delete with
     * {@code force=true} to remove it anyway.
     */
    public static final String EMAIL_PROVIDER_IN_USE = "EMAIL_PROVIDER_IN_USE";
    /**
     * Captcha is bring-your-own-keys: turning it on (or switching provider) needs that
     * provider's site key and secret stored first.
     */
    public static final String CAPTCHA_KEYS_REQUIRED = "CAPTCHA_KEYS_REQUIRED";
    /**
     * A captcha key a live flow uses cannot be removed ({@code meta.flows}); delete with
     * {@code force=true} to remove it anyway.
     */
    public static final String CAPTCHA_IN_USE = "CAPTCHA_IN_USE";
    /** The development email sender is not available on a production instance (409). */
    public static final String DEVELOPMENT_EMAIL_NOT_ALLOWED = "DEVELOPMENT_EMAIL_NOT_ALLOWED";
    /** The development email sender is not available on this deployment (503). */
    public static final String DEVELOPMENT_EMAIL_UNAVAILABLE = "DEVELOPMENT_EMAIL_UNAVAILABLE";
    /** The development sender's daily or per-recipient hourly cap is spent (429; {@code meta.window}, {@code meta.retry_after_seconds}). */
    public static final String DEVELOPMENT_EMAIL_LIMIT_REACHED = "DEVELOPMENT_EMAIL_LIMIT_REACHED";
    /** The development sender sends only Atlas's own sign-in emails (422). */
    public static final String DEVELOPMENT_EMAIL_TEMPLATE_NOT_ALLOWED = "DEVELOPMENT_EMAIL_TEMPLATE_NOT_ALLOWED";
    /** Referrals cannot be enabled until the provider's needs are set (422; {@code meta} names what is missing). */
    public static final String REFERRALS_NOT_CONFIGURED = "REFERRALS_NOT_CONFIGURED";
    /** Another user on the instance already holds that referral code (409). */
    public static final String REFERRAL_CODE_TAKEN = "REFERRAL_CODE_TAKEN";
    /** A billing plan cannot be changed or removed while subscriptions use it. */
    public static final String PLAN_IN_USE = "PLAN_IN_USE";
    public static final String LAST_ADMIN = "LAST_ADMIN";
    public static final String IDENTIFIER_EXISTS = "IDENTIFIER_EXISTS";
    public static final String IDENTIFIER_NOT_FOUND = "IDENTIFIER_NOT_FOUND";
    /** A usage debit would exceed one of the API key's spend caps (the {@code code} of a refused debit). */
    public static final String LIMIT_EXCEEDED = "LIMIT_EXCEEDED";
    /** 422 — managed WAF provision/deprovision on an instance with no WAF configuration saved ({@code meta.action}). */
    public static final String MANAGED_WAF_NOT_CONFIGURED = "MANAGED_WAF_NOT_CONFIGURED";
    /** 403 — a Sign in with Atlas test or review account tried something outside the app(s) it is confined to (another app, the Atlas dashboard, account settings). */
    public static final String TEST_ACCOUNT_RESTRICTED = "TEST_ACCOUNT_RESTRICTED";
    /** 409 — the account already holds the maximum number of test and review accounts (25 by default, across all its apps). */
    public static final String PROVISIONED_ACCOUNT_LIMIT_REACHED = "PROVISIONED_ACCOUNT_LIMIT_REACHED";
    /** 422 — the setting or address needs a DNS-verified domain on the account (test mode, the verified-domains-only restriction, a test-account address off your domains), or an expired account tried to sign in. */
    public static final String DOMAIN_NOT_VERIFIED = "DOMAIN_NOT_VERIFIED";
    /** The session was revoked. */
    public static final String SESSION_REVOKED = "SESSION_REVOKED";
    /** The session timed out from inactivity. */
    public static final String SESSION_IDLE_EXPIRED = "SESSION_IDLE_EXPIRED";
    /** The session passed its absolute expiry. */
    public static final String SESSION_EXPIRED = "SESSION_EXPIRED";
}
