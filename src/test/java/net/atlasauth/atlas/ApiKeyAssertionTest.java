package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Key-pair API key assertions: header, claims, and a signature that verifies with the public key. */
class ApiKeyAssertionTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    private static String pem(KeyPair kp) {
        return "-----BEGIN PRIVATE KEY-----\n" + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(kp.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";
    }

    private static JsonNode part(String segment) throws Exception {
        return JSON.readTree(Base64.getUrlDecoder().decode(segment));
    }

    @Test
    void es256IsRawRsAndVerifies() throws Exception {
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair kp = g.generateKeyPair();
        String[] p = ApiKeyAssertion.sign(pem(kp), "key_1", "ins_1", 300, "read", "j1").split("\\.");
        JsonNode h = part(p[0]);
        JsonNode c = part(p[1]);
        assertEquals("ES256", h.get("alg").asText());
        assertEquals("key_1", h.get("kid").asText());
        assertEquals("JWT", h.get("typ").asText());
        assertEquals("key_1", c.get("iss").asText());
        assertEquals("ins_1", c.get("aud").asText());
        assertEquals("read", c.get("scope").asText());
        assertEquals("j1", c.get("jti").asText());
        assertEquals(300, c.get("exp").asLong() - c.get("iat").asLong());
        byte[] sig = Base64.getUrlDecoder().decode(p[2]);
        assertEquals(64, sig.length);
        Signature v = Signature.getInstance("SHA256withECDSAinP1363Format");
        v.initVerify(kp.getPublic());
        v.update((p[0] + "." + p[1]).getBytes(StandardCharsets.US_ASCII));
        assertTrue(v.verify(sig));
    }

    @Test
    void eddsaVerifies() throws Exception {
        KeyPair kp = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        String[] p = ApiKeyAssertion.sign(pem(kp), "key_2", "ins_1").split("\\.");
        assertEquals("EdDSA", part(p[0]).get("alg").asText());
        assertNotNull(part(p[1]).get("jti"));
        Signature v = Signature.getInstance("Ed25519");
        v.initVerify(kp.getPublic());
        v.update((p[0] + "." + p[1]).getBytes(StandardCharsets.US_ASCII));
        assertTrue(v.verify(Base64.getUrlDecoder().decode(p[2])));
    }
}
