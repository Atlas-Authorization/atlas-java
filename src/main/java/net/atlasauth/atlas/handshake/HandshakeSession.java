package net.atlasauth.atlas.handshake;

/**
 * A fresh first-party session, minted by exchanging a single-use handshake nonce.
 *
 * <p>These are the cookie <em>values</em> a satellite property sets as its OWN
 * first-party cookies after {@link Handshake#redeemHandshake}: {@link #jwt()} as
 * {@code __session} (script-readable, short-lived) and {@link #refreshToken()} as
 * {@code __atlas_rt} (HttpOnly). See {@link Handshake} for the full example.
 */
public record HandshakeSession(
        /** The {@code __session} JWT value (script-readable, short-lived). */
        String jwt,
        /** The {@code __atlas_rt} refresh value to set as an HttpOnly cookie. */
        String refreshToken,
        String sessionId,
        /** Seconds until the {@code __session} JWT expires. */
        long expiresIn) {
}
