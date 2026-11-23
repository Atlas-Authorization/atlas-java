package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.ApiKeyAssertion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The key mirror verifies key-pair assertions locally with the server's refusal codes; a disabled row is refused. */
class KeyMirrorAssertionTest {
    private static final ObjectMapper M = new ObjectMapper();

    private final AtomicLong offsetMs = new AtomicLong();
    private final MemoryKeyMirrorStore store = new MemoryKeyMirrorStore();
    private String privatePem;
    private KeyMirror mirror;

    private static String pem(String label, byte[] der) {
        return "-----BEGIN " + label + "-----\n" + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(der) + "\n-----END " + label + "-----\n";
    }

    private static Backend backend(JsonNode row) {
        return new Backend() {
            @Override
            public JsonNode createApiKey(Map<String, Object> body, String k) {
                return row;
            }

            @Override
            public void deleteApiKey(String id) {
            }

            @Override
            public JsonNode listRevokedApiKeys(String since, int limit) {
                return M.createObjectNode();
            }
        };
    }

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair kp = g.generateKeyPair();
        privatePem = pem("PRIVATE KEY", kp.getPrivate().getEncoded());
        JsonNode row = M.valueToTree(Map.of("id", "key_1", "subject_type", "machine", "subject_id", "m1", "scopes", List.of("read", "write"),
                "credential", "key_pair", "audience", "ins_1", "public_key", pem("PUBLIC KEY", kp.getPublic().getEncoded())));
        mirror = KeyMirror.builder(backend(row), store).clock(() -> System.currentTimeMillis() + offsetMs.get()).build();
        mirror.mint(Map.of(), null);
    }

    private String sign(String audience, long ttl, String scope, String jti) {
        return ApiKeyAssertion.sign(privatePem, "key_1", audience, ttl, scope, jti);
    }

    @Test
    void validAssertionNarrowsScope() {
        KeyMirror.Verification v = mirror.verifyAssertion(sign("ins_1", 300, "read", "a"), List.of("read"), null);
        assertTrue(v.valid());
        assertEquals("key_pair", v.credential());
        assertEquals("a", v.jti());
        assertEquals(List.of("read"), v.effectiveScopes());
        assertEquals("scope_not_granted", mirror.verifyAssertion(sign("ins_1", 300, "admin", null), List.of(), null).code());
    }

    @Test
    void refusalCodesAndJtiClaimedLast() {
        assertEquals("wrong_audience", mirror.verifyAssertion(sign("other", 300, null, null), List.of(), null).code());
        assertEquals("assertion_expired", mirror.verifyAssertion(sign("ins_1", -100, null, null), List.of(), null).code());
        assertEquals("assertion_lifetime_too_long", mirror.verifyAssertion(sign("ins_1", 7200, null, null), List.of(), null).code());
        assertEquals("invalid_assertion", mirror.verifyAssertion("a.b.c", List.of(), null).code());
        String token = sign("ins_1", 300, null, "j1");
        assertEquals("insufficient_scope", mirror.verifyAssertion(token, List.of("nope"), null).code());
        assertTrue(mirror.verifyAssertion(token, List.of(), null).valid());
        assertEquals("assertion_replayed", mirror.verifyAssertion(token, List.of(), null).code());
        offsetMs.set(-600_000);
        assertEquals("assertion_not_yet_valid", mirror.verifyAssertion(sign("ins_1", 300, null, null), List.of(), null).code());
    }

    @Test
    void disabledAndRevokedRowsAreRefusedAndWebhookCopiesEnabled() throws Exception {
        store.put(store.get("key_1").withEnabled(false));
        assertEquals("invalid_assertion", mirror.verifyAssertion(sign("ins_1", 300, null, null), List.of(), null).code());
        assertTrue(mirror.applyWebhook("api_key.updated", M.readTree("{\"id\":\"key_1\",\"enabled\":true}")));
        assertTrue(mirror.verifyAssertion(sign("ins_1", 300, null, null), List.of(), null).valid());
        store.markRevoked("key_1", 1, null);
        assertFalse(mirror.verifyAssertion(sign("ins_1", 300, null, null), List.of(), null).valid());

        store.put(new MirroredApiKey("k2", KeyMirror.hashApiKeySecret("s"), null, "user", "u", null, List.of(), null, null, null, null, null)
                .withEnabled(false));
        assertFalse(mirror.verify("s").valid());
        mirror.applyWebhook("api_key.updated", M.readTree("{\"id\":\"k2\",\"enabled\":true}"));
        assertTrue(mirror.verify("s").valid());
        assertNull(mirror.verify("s").code());
    }
}
