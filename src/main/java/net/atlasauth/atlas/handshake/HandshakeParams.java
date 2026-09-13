package net.atlasauth.atlas.handshake;

/**
 * The handshake parameters carried on a satellite's return URL: the (non-secret)
 * user id and the single-use nonce. Only these travel in the URL — never a token.
 * Feed them to {@link Handshake#redeemHandshake} to obtain a {@link HandshakeSession}.
 */
public record HandshakeParams(
        /** The {@code __atlas_hu} value from the return URL. */
        String userId,
        /** The single-use {@code __atlas_hn} nonce from the return URL. */
        String nonce) {
}
