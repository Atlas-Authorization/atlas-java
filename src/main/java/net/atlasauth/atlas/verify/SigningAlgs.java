package net.atlasauth.atlas.verify;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;

/**
 * The JWS algorithms Atlas signs tokens with, and the signature check every verifier here shares. An instance key is
 * RS256 (the default), ES256, ES384, PS256 or EdDSA (Ed25519). Verification is pinned to exactly this list ({@code none}
 * and HMAC never verify), and the token's {@code kid} selects one published key whose TYPE must fit the algorithm: RS256
 * and PS256 need an RSA key, ES256 a P-256 key, ES384 a P-384 key, EdDSA an Ed25519 key. A key can never verify an
 * algorithm of another family (no algorithm confusion), and a JWK that declares an {@code alg} only verifies that one.
 */
public final class SigningAlgs {

    /** RS256, ES256, ES384, PS256 and EdDSA. */
    public static final List<JWSAlgorithm> ALGS = List.of(JWSAlgorithm.RS256, JWSAlgorithm.ES256, JWSAlgorithm.ES384, JWSAlgorithm.PS256, JWSAlgorithm.EdDSA);

    private SigningAlgs() {
    }

    /** Whether the token's header names an allowed algorithm. */
    public static boolean allowed(SignedJWT jwt) {
        return ALGS.contains(jwt.getHeader().getAlgorithm());
    }

    /** Verify the signature with the key {@code kid} selects (any key when {@code kid} is null). */
    public static boolean verify(SignedJWT jwt, JWKSet keys, String kid) {
        JWSAlgorithm alg = jwt.getHeader().getAlgorithm();
        if (!ALGS.contains(alg)) {
            return false;
        }
        for (JWK key : keys.getKeys()) {
            if (kid != null && !kid.equals(key.getKeyID())) {
                continue;
            }
            if (key.getAlgorithm() != null && !key.getAlgorithm().getName().equals(alg.getName())) {
                continue;
            }
            try {
                if ((JWSAlgorithm.RS256.equals(alg) || JWSAlgorithm.PS256.equals(alg)) && key instanceof RSAKey rsa) {
                    if (jwt.verify(new RSASSAVerifier(rsa.toRSAPublicKey()))) {
                        return true;
                    }
                } else if (JWSAlgorithm.ES256.equals(alg) && key instanceof ECKey ec && Curve.P_256.equals(ec.getCurve())) {
                    if (jwt.verify(new ECDSAVerifier(ec.toECPublicKey()))) {
                        return true;
                    }
                } else if (JWSAlgorithm.ES384.equals(alg) && key instanceof ECKey ec && Curve.P_384.equals(ec.getCurve())) {
                    if (jwt.verify(new ECDSAVerifier(ec.toECPublicKey()))) {
                        return true;
                    }
                } else if (JWSAlgorithm.EdDSA.equals(alg) && key instanceof OctetKeyPair okp && Curve.Ed25519.equals(okp.getCurve())) {
                    // done with the JDK: Nimbus's EdDSA verifier needs an extra crypto library
                    byte[] prefix = {0x30, 0x2a, 0x30, 0x05, 0x06, 0x03, 0x2b, 0x65, 0x70, 0x03, 0x21, 0x00};
                    byte[] x = okp.getDecodedX();
                    byte[] der = Arrays.copyOf(prefix, prefix.length + x.length);
                    System.arraycopy(x, 0, der, prefix.length, x.length);
                    PublicKey pub = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(der));
                    Signature s = Signature.getInstance("Ed25519");
                    s.initVerify(pub);
                    s.update(jwt.getSigningInput());
                    if (s.verify(jwt.getSignature().decode())) {
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
