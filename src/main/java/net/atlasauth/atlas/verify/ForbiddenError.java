package net.atlasauth.atlas.verify;

/**
 * Thrown by {@code protect(...)} when the claims do not satisfy a
 * {@link ProtectCondition}. An application maps this to a 401/403 response. The
 * {@link #reason()} is one of {@code signed-out}, {@code role}, {@code permission}.
 */
public class ForbiddenError extends RuntimeException {
    private final String reason;
    private final ProtectCondition condition;

    public ForbiddenError(String reason, ProtectCondition condition) {
        super("Forbidden: " + reason);
        this.reason = reason;
        this.condition = condition;
    }

    public String reason() {
        return reason;
    }

    public ProtectCondition condition() {
        return condition;
    }
}
