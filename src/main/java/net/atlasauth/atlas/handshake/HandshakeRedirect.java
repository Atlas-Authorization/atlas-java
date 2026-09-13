package net.atlasauth.atlas.handshake;

/**
 * The one-time redirect a satellite sends the browser to when it has no local
 * session, plus the PKCE verifier it must remember for the redeem that follows.
 *
 * <p>Returned by {@link Handshake#handshakeRedirect}. Send the browser to
 * {@link #url()} (which already carries the {@code code_challenge}) and stash
 * {@link #codeVerifier()} server-side — documented as an HttpOnly cookie
 * {@code __atlas_hv}, path {@code /}, ~300s — so the return trip can pass it to
 * {@link Handshake#redeemHandshake}. The verifier is a secret and must never
 * appear in {@link #url()} or any other URL.
 */
public record HandshakeRedirect(
        /** The {@code {fapiOrigin}/v1/client/handshake?...&code_challenge=...} URL to redirect the browser to. */
        String url,
        /** The PKCE verifier to stash server-side ({@code __atlas_hv}) and later hand to redeem. */
        String codeVerifier) {
}
