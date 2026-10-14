package net.atlasauth.atlas.verify;

/** Thrown by {@link LogoutTokenVerifier} when a {@code logout_token} is not valid. Answer the request with 400. */
public final class LogoutTokenException extends RuntimeException {

    /** Why the token was refused. */
    public enum Reason {
        MALFORMED,
        BAD_ALGORITHM,
        WRONG_TYPE,
        NO_KEYS,
        BAD_SIGNATURE,
        WRONG_ISSUER,
        WRONG_AUDIENCE,
        MISSING_IAT,
        ISSUED_IN_FUTURE,
        EXPIRED,
        MISSING_JTI,
        MISSING_EVENT,
        HAS_NONCE,
        MISSING_SUBJECT
    }

    private final Reason reason;

    public LogoutTokenException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }
}
