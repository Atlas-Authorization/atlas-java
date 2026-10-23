package net.atlasauth.atlas.verify;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import java.util.List;

/**
 * The JWS algorithms Atlas signs tokens with, and the signature check every verifier here shares.
 * An instance key is RS256 (the default) or ES256, and an API may use either. Verification is
 * pinned to exactly this list — {@code none} and HMAC never verify — and the token's {@code kid}
 * selects one published key whose type fixes which of the two applies (an RSA key verifies only
 * RS256, a P-256 key only ES256).
 */
public final class SigningAlgs {

    /** RS256 and ES256. */
    public static final List<JWSAlgorithm> ALGS = List.of(JWSAlgorithm.RS256, JWSAlgorithm.ES256);

    private SigningAlgs() {
    }

    /** Whether the token's header names an allowed algorithm. */
    public static boolean allowed(SignedJWT jwt) {
        return ALGS.contains(jwt.getHeader().getAlgorithm());
    }

    /** Verify the signature with the key {@code kid} selects (any key when {@code kid} is null). */
    public static boolean verify(SignedJWT jwt, JWKSet keys, String kid) {
        JWSAlgorithm alg = jwt.getHeader().getAlgorithm();
        for (JWK key : keys.getKeys()) {
            if (kid != null && !kid.equals(key.getKeyID())) {
                continue;
            }
            if (key.getAlgorithm() != null && !key.getAlgorithm().getName().equals(alg.getName())) {
                continue;
            }
            try {
                if (JWSAlgorithm.RS256.equals(alg) && key instanceof RSAKey rsa) {
                    if (jwt.verify(new RSASSAVerifier(rsa.toRSAPublicKey()))) {
                        return true;
                    }
                } else if (JWSAlgorithm.ES256.equals(alg) && key instanceof ECKey ec && Curve.P_256.equals(ec.getCurve())) {
                    if (jwt.verify(new ECDSAVerifier(ec.toECPublicKey()))) {
                        return true;
                    }
                }
            } catch (Exception ignored) {
                // try the next key
            }
        }
        return false;
    }
}
