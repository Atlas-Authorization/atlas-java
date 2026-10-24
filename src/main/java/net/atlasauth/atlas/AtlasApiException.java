package net.atlasauth.atlas;

import java.util.List;

/**
 * Thrown on any non-2xx response from the Backend API.
 *
 * <p>Carries the HTTP {@link #status()} and the full parsed {@link #errors()}
 * envelope. Branch on {@link #code()} (the first error's stable code) or use
 * {@link #hasCode(String)}.
 *
 * <p>The transport instantiates the most specific subclass it can for the status
 * ({@link AuthenticationException} 401, {@link PermissionException} 403,
 * {@link NotFoundException} 404, {@link ConflictException} 409,
 * {@link InvalidRequestException} 400/422, {@link RateLimitException} 429,
 * {@link ServerException} 5xx); all remain instances of {@code AtlasApiException},
 * so a caller can catch broadly or narrowly.
 */
public class AtlasApiException extends AtlasException {

    private final int status;
    private final List<ErrorItem> errors;

    public AtlasApiException(int status, List<ErrorItem> errors) {
        super(firstMessage(status, errors));
        this.status = status;
        this.errors = List.copyOf(errors);
    }

    private static String firstMessage(int status, List<ErrorItem> errors) {
        if (errors != null && !errors.isEmpty() && errors.get(0).message() != null) {
            return errors.get(0).message();
        }
        return "Atlas API request failed with status " + status;
    }

    /** HTTP status of the failed response. */
    public int status() {
        return status;
    }

    /** The full &sect;9.1 error envelope, in order. */
    public List<ErrorItem> errors() {
        return errors;
    }

    /** The first error's stable code, the field callers branch on most. */
    public String code() {
        return errors.isEmpty() ? null : errors.get(0).code();
    }

    /** True when any error in the envelope carries the given stable code. */
    public boolean hasCode(String code) {
        return errors.stream().anyMatch(e -> code.equals(e.code()));
    }

    /** True when the key lacked a required scope: accepts {@code INSUFFICIENT_SCOPE} and the former {@code SCOPE_MISSING}. */
    public boolean isInsufficientScope() {
        return hasCode(ErrorCodes.INSUFFICIENT_SCOPE) || hasCode(ErrorCodes.SCOPE_MISSING);
    }

    /** The scope an {@code INSUFFICIENT_SCOPE} (or {@code SCOPE_MISSING}) error says the call needed, or null. */
    public String requiredScope() {
        for (ErrorItem e : errors) {
            if ((ErrorCodes.INSUFFICIENT_SCOPE.equals(e.code()) || ErrorCodes.SCOPE_MISSING.equals(e.code())) && e.meta() != null) {
                Object v = e.meta().get("required_scope");
                if (v instanceof String s) {
                    return s;
                }
            }
        }
        return null;
    }

    /** True when a multi-factor sign-in is needed first ({@code STEP_UP_REQUIRED}). */
    public boolean isStepUpRequired() {
        return hasCode(ErrorCodes.STEP_UP_REQUIRED);
    }

    /** True when no endpoint matched the method and path ({@code ROUTE_NOT_FOUND}). */
    public boolean isRouteNotFound() {
        return hasCode(ErrorCodes.ROUTE_NOT_FOUND);
    }

    /** The closest documented paths of a {@code ROUTE_NOT_FOUND} error, or an empty list. */
    public List<String> didYouMean() {
        for (ErrorItem e : errors) {
            if (ErrorCodes.ROUTE_NOT_FOUND.equals(e.code()) && e.didYouMean() != null) {
                return e.didYouMean();
            }
        }
        return List.of();
    }

    /**
     * Build the most specific {@code AtlasApiException} subclass for a status.
     */
    public static AtlasApiException of(int status, List<ErrorItem> errors) {
        return switch (status) {
            case 400, 422 -> new InvalidRequestException(status, errors);
            case 401 -> new AuthenticationException(status, errors);
            case 403 -> new PermissionException(status, errors);
            case 404 -> new NotFoundException(status, errors);
            case 409 -> new ConflictException(status, errors);
            case 429 -> new RateLimitException(status, errors);
            default -> status >= 500
                    ? new ServerException(status, errors)
                    : new AtlasApiException(status, errors);
        };
    }
}
