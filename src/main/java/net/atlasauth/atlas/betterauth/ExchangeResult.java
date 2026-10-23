package net.atlasauth.atlas.betterauth;

/**
 * The classified outcome of a Better Auth session exchange.
 *
 * <ul>
 *   <li>{@link Kind#SESSION}: {@code jwt}, {@code refreshToken}, {@code sessionId}, {@code userId}, {@code expiresIn}.</li>
 *   <li>{@link Kind#NEEDS_MFA}: {@code attemptId} and {@code status} ({@code needs_second_factor} /
 *       {@code needs_mfa_enrollment}). The Better Auth session is spent; the user finishes the second factor.</li>
 *   <li>{@link Kind#REFUSED}: {@code outcome} ({@code closed}, {@code invalid}, {@code signature_invalid},
 *       {@code expired}, {@code reused}, {@code user_unavailable}, {@code rate_limited}, or {@code unavailable}
 *       for anything that is not a verdict on the session); {@code httpStatus} is 0 for a transport failure.</li>
 * </ul>
 */
public record ExchangeResult(
        Kind kind,
        String jwt,
        String refreshToken,
        String sessionId,
        String userId,
        long expiresIn,
        String attemptId,
        String status,
        String outcome,
        int httpStatus) {

    public enum Kind { SESSION, NEEDS_MFA, REFUSED }

    public static ExchangeResult session(String jwt, String refreshToken, String sessionId, String userId, long expiresIn) {
        return new ExchangeResult(Kind.SESSION, jwt, refreshToken, sessionId, userId, expiresIn, "", "", null, 200);
    }

    public static ExchangeResult needsMfa(String attemptId, String status) {
        return new ExchangeResult(Kind.NEEDS_MFA, "", "", "", "", 0, attemptId, status, null, 200);
    }

    public static ExchangeResult refused(String outcome, int httpStatus) {
        return new ExchangeResult(Kind.REFUSED, "", "", "", "", 0, "", "", outcome, httpStatus);
    }

    /** True when the refusal says the Better Auth session will never exchange (its cookies should go). */
    public boolean isFinalRefusal() {
        return kind == Kind.REFUSED && BetterAuth.FINAL_OUTCOMES.contains(outcome);
    }
}
