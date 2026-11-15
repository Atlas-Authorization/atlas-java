package net.atlasauth.atlas;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import net.atlasauth.atlas.verify.SigningAlgs;
import net.atlasauth.atlas.verify.TransactionTokenVerifier;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/** Every algorithm an instance can sign with verifies, and no key verifies another family's algorithm. */
class AlgorithmsTest {

    private static boolean verifies(TestJwt key, String token) throws Exception {
        SignedJWT jwt = SignedJWT.parse(token);
        return SigningAlgs.allowed(jwt) && SigningAlgs.verify(jwt, new JWKSet(key.publicJwk()), key.kid);
    }

    @Test
    void allFiveAlgorithmsVerifyWithTheirOwnKeyType() throws Exception {
        for (String alg : TestJwt.ALGS) {
            TestJwt key = TestJwt.generate(alg);
            assertTrue(verifies(key, key.sign("{\"sub\":\"u\"}")), alg);
        }
    }

    @Test
    void aKeyNeverVerifiesAnotherFamilyOrCurve() throws Exception {
        TestJwt rsa = TestJwt.generate("RS256");
        TestJwt p256 = TestJwt.generate("ES256");
        TestJwt p384 = TestJwt.generate("ES384");
        TestJwt ed = TestJwt.generate("EdDSA");
        // signed by one key but claiming another algorithm: the JWK of the claimed family never matches the signer's family
        assertFalse(verifies(rsa, rsa.sign("ES256", null, "{}")));
        assertFalse(verifies(p256, p256.sign("ES384", null, "{}")), "P-256 key under ES384");
        assertFalse(verifies(p384, p384.sign("ES256", null, "{}")), "P-384 key under ES256");
        assertFalse(verifies(ed, ed.sign("RS256", null, "{}")));
        assertFalse(verifies(rsa, rsa.sign("EdDSA", null, "{}")));
        // a JWK that declares an alg verifies only that alg
        TestJwt ps = TestJwt.generate("PS256");
        String rs = ps.sign("RS256", null, "{}");
        assertFalse(SigningAlgs.verify(SignedJWT.parse(rs), new JWKSet(new com.nimbusds.jose.jwk.RSAKey.Builder((java.security.interfaces.RSAPublicKey) ps.pair.getPublic())
                .keyID(ps.kid).algorithm(com.nimbusds.jose.JWSAlgorithm.PS256).build()), ps.kid));
    }

    @Test
    void noneAndHmacAreNeverAllowed() throws Exception {
        Base64.Encoder e = Base64.getUrlEncoder().withoutPadding();
        for (String alg : new String[]{"none", "HS256", "HS512"}) {
            String token = e.encodeToString(("{\"alg\":\"" + alg + "\"}").getBytes()) + "." + e.encodeToString("{\"sub\":\"u\"}".getBytes()) + "." + e.encodeToString("sig".getBytes());
            try {
                assertFalse(SigningAlgs.allowed(SignedJWT.parse(token)), alg);
            } catch (java.text.ParseException unparseable) {
                // refused even earlier
            }
        }
    }

    @Test
    void transactionTokensAcceptAllFiveAndRefuseOtherTrustDomains() throws Exception {
        long now = System.currentTimeMillis() / 1000;
        String claims = "{\"iss\":\"https://auth.example.com\",\"aud\":\"pay.example.com\",\"sub\":\"u\",\"txn\":\"t1\",\"scope\":\"a b\",\"req_wl\":\"w\",\"iat\":" + now + ",\"exp\":" + (now + 60) + "}";
        for (String alg : TestJwt.ALGS) {
            TestJwt key = TestJwt.generate(alg);
            var jwks = new JWKSet(key.publicJwk());
            var ok = TransactionTokenVerifier.forJwks("https://auth.example.com", "pay.example.com", jwks).verify(key.sign(alg, "txntoken+jwt", claims));
            assertTrue(ok.ok(), alg);
            assertEquals(java.util.List.of("a", "b"), ok.scopes());
            assertFalse(TransactionTokenVerifier.forJwks("https://auth.example.com", "other.example.com", jwks).verify(key.sign(alg, "txntoken+jwt", claims)).ok());
            assertFalse(TransactionTokenVerifier.forJwks("https://auth.example.com", "pay.example.com", jwks).verify(key.sign(alg, "jwt", claims)).ok(), "wrong typ");
        }
    }
}
