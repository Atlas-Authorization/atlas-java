package net.atlasauth.atlas.rp;

/**
 * A sign-in failed. {@link #code()} is one of {@code state_mismatch}, {@code issuer_mismatch},
 * {@code access_denied}, {@code invalid_request}, {@code invalid_token}, {@code discovery_failed},
 * {@code reauthentication_required}, {@code subject_mismatch}, {@code token_exchange_failed}. Send
 * {@link #clearCookie()} as {@code Set-Cookie} on your error response so the spent flow cookie is dropped.
 */
public class OidcException extends RuntimeException {
    private final String code;
    private String clearCookie;

    public OidcException(String code, String message) {
        super(message);
        this.code = code;
    }

    public OidcException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String code() {
        return code;
    }

    /** The {@code Set-Cookie} value that clears the sign-in cookie; set once a callback ran. */
    public String clearCookie() {
        return clearCookie;
    }

    void clearCookie(String clearCookie) {
        this.clearCookie = clearCookie;
    }
}
