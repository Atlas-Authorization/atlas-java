package net.atlasauth.atlas.machine;

import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class MachineClientTest {

    /** RFC 8032 section 7.1 test 1: the documented wire-spec vector (empty message). */
    @Test
    void signsThePureEd25519VectorAndEncodesBase64Url() throws Exception {
        PrivateKey key = DpopKey.privateKeyFromSeedHex("9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60");
        String pem = DpopKey.pem("PRIVATE KEY", key.getEncoded());
        String expectedHex = "e5564300c360ac729086e2cc806e828a84877f1eb8e5d974d873e065224901555fb8821590a33bacc61e39701cf9b46bd25bf5f0595bbe24655141438e7a100b";
        byte[] expected = DpopKey.hexBytes(expectedHex);
        assertEquals(Base64.getUrlEncoder().withoutPadding().encodeToString(expected), MachineClient.signChallenge(pem, ""));
        assertNotEquals(MachineClient.signChallenge(pem, "nonce-a"), MachineClient.signChallenge(pem, "nonce-b"));
    }

    @Test
    void dpopProofCarriesTheKeyThumbprintAndNamesTheToken() {
        DpopKey key = DpopKey.generate();
        String proof = key.proof("post", "https://api.example.com/x?y=1", "tok", null, 1_700_000_000_000L);
        String[] parts = proof.split("\\.");
        assertEquals(3, parts.length);
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        assertTrue(payload.contains("\"htm\":\"POST\""));
        assertTrue(payload.contains("\"htu\":\"https://api.example.com/x\""));
        assertTrue(payload.contains("\"ath\""));
        assertEquals(43, key.thumbprint().length());
        DpopKey again = DpopKey.fromPem(key.privateKeyPem(), key.publicKeyPem());
        assertEquals(key.thumbprint(), again.thumbprint());
    }

    @Test
    void getTokenBeforeEnrolmentFailsClearly() {
        MachineClient c = MachineClient.builder("https://clerk.example.com", MachineStore.memory()).build();
        MachineClientException e = assertThrows(MachineClientException.class, c::getToken);
        assertEquals("not_enrolled", e.code());
        assertFalse(c.isEnrolled());
    }
}
