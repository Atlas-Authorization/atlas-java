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
    /** The key lacks the scope this route needs. */
    public static final String SCOPE_MISSING = "SCOPE_MISSING";
    /** Email sending is not set up for this instance. */
    public static final String EMAIL_DELIVERY_NOT_CONFIGURED = "EMAIL_DELIVERY_NOT_CONFIGURED";
    /** The operation needs an email provider to be configured first. */
    public static final String EMAIL_PROVIDER_REQUIRED = "EMAIL_PROVIDER_REQUIRED";
    /** A billing plan cannot be changed or removed while subscriptions use it. */
    public static final String PLAN_IN_USE = "PLAN_IN_USE";
    public static final String LAST_ADMIN = "LAST_ADMIN";
    public static final String IDENTIFIER_EXISTS = "IDENTIFIER_EXISTS";
    public static final String IDENTIFIER_NOT_FOUND = "IDENTIFIER_NOT_FOUND";
    /** A usage debit would exceed one of the API key's spend caps (the {@code code} of a refused debit). */
    public static final String LIMIT_EXCEEDED = "LIMIT_EXCEEDED";
    /** The session was revoked. */
    public static final String SESSION_REVOKED = "SESSION_REVOKED";
    /** The session timed out from inactivity. */
    public static final String SESSION_IDLE_EXPIRED = "SESSION_IDLE_EXPIRED";
    /** The session passed its absolute expiry. */
    public static final String SESSION_EXPIRED = "SESSION_EXPIRED";
}
