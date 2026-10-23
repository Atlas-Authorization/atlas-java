package net.atlasauth.atlas.verify;

/**
 * Hands out the server nonce a DPoP proof must carry (RFC 9449 §8) and checks one. {@link #current()}
 * goes to the client in the {@code DPoP-Nonce} header; {@link #valid(String)} accepts the nonces
 * still in force.
 */
public interface DpopNonceSource {
    String current();

    boolean valid(String nonce);
}
