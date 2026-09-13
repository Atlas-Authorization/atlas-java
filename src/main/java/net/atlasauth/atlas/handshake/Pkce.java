package net.atlasauth.atlas.handshake;

/**
 * A PKCE (RFC 7636, {@code S256}) pair binding a handshake to the browser that
 * started it.
 *
 * <p>The handshake nonce rides back on a return URL, and return URLs get logged
 * (proxies, access logs, {@code Referer} headers, browser history). A nonce alone
 * is therefore a <em>bearer</em> credential: anyone who scrapes it from a log
 * could redeem it. PKCE removes that: the satellite keeps the high-entropy
 * {@link #verifier()} server-side (never in any URL) and sends only its SHA-256
 * {@link #challenge()} up front on the handshake bounce. Redeem must then present
 * the matching verifier, so a stolen nonce is useless without the secret that
 * never left the origin that minted it.
 *
 * <p>Produced by {@link Handshake#generatePkce()}: {@link #verifier()} is the
 * base64url (no padding) of 32 {@link java.security.SecureRandom} bytes, and
 * {@link #challenge()} is the base64url (no padding) of {@code SHA-256} over the
 * verifier's US-ASCII bytes.
 */
public record Pkce(
        /**
         * The secret to stash server-side (documented as an HttpOnly cookie
         * {@code __atlas_hv}, path {@code /}, ~300s) and hand to
         * {@link Handshake#redeemHandshake}. Never put this in a URL.
         */
        String verifier,
        /**
         * The {@code code_challenge} to append to the handshake bounce URL — safe
         * to expose, as it is a one-way hash of the verifier.
         */
        String challenge) {
}
