package net.atlasauth.atlas.verify;

/**
 * The outcome of verifying a session token — either an authorized session
 * carrying the claims and the bound {@link #has}/{@link #protect} helpers, or a
 * failure with a single {@link #reason()}.
 *
 * <p>Following {@code verify.ts}, a failure gives one reason for every cause:
 * telling a caller whether the signature, the issuer, or the expiry was wrong
 * helps someone refining a forged token more than it helps a developer debug a
 * real one.
 */
public final class VerifyResult {

    /** Why verification failed. */
    public enum Reason {
        MALFORMED,
        INVALID,
        NO_KEYS,
        UNAUTHORIZED_PARTY
    }

    private final boolean ok;
    private final SessionClaims claims;
    private final Reason reason;

    private VerifyResult(boolean ok, SessionClaims claims, Reason reason) {
        this.ok = ok;
        this.claims = claims;
        this.reason = reason;
    }

    public static VerifyResult authorized(SessionClaims claims) {
        return new VerifyResult(true, claims, null);
    }

    public static VerifyResult failed(Reason reason) {
        return new VerifyResult(false, null, reason);
    }

    public boolean ok() {
        return ok;
    }

    /** The verified claims, or {@code null} when {@link #ok()} is false. */
    public SessionClaims claims() {
        return claims;
    }

    /** The failure reason, or {@code null} when {@link #ok()} is true. */
    public Reason reason() {
        return reason;
    }

    /** True when the claims satisfy the condition. Throws if the result is a failure. */
    public boolean has(ProtectCondition condition) {
        requireOk();
        return Authz.has(claims, condition);
    }

    public boolean has() {
        return has(ProtectCondition.none());
    }

    /** Assert the condition; returns the claims on success, throws {@link ForbiddenError} otherwise. */
    public SessionClaims protect(ProtectCondition condition) {
        requireOk();
        return Authz.protect(claims, condition);
    }

    public SessionClaims protect() {
        return protect(ProtectCondition.none());
    }

    private void requireOk() {
        if (!ok) {
            throw new IllegalStateException("Cannot authorize an unverified session (reason=" + reason + ")");
        }
    }
}
