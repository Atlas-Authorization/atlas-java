package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class KeyMirrorTest {

    private static final ObjectMapper M = new ObjectMapper();

    private static JsonNode json(String s) {
        try {
            return M.readTree(s);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Backend backend(String[] revokedPage, List<String> deleted) {
        return new Backend() {
            @Override
            public JsonNode createApiKey(Map<String, Object> body, String k) {
                return json("{\"id\":\"eak_1\",\"prefix\":\"ak_abc\",\"subject_type\":\"user\",\"subject_id\":\"usr_1\",\"scopes\":[\"read\"],\"secret\":\"ak_secret_1\"}");
            }

            @Override
            public void deleteApiKey(String id) {
                deleted.add(id);
            }

            @Override
            public JsonNode listRevokedApiKeys(String since, int limit) {
                return json(revokedPage[0]);
            }
        };
    }

    @Test
    void mintVerifyRevokeAndSyncWithoutANetworkCallOnTheHotPath() {
        List<String> deleted = new java.util.ArrayList<>();
        String[] page = {"{\"data\":[],\"has_more\":false,\"next_cursor\":null}"};
        MemoryKeyMirrorStore store = new MemoryKeyMirrorStore();
        AtomicLong now = new AtomicLong(1000);
        KeyMirror m = KeyMirror.builder(backend(page, deleted), store).maxStalenessMs(500).clock(now::get).build();
        m.mint(Map.of("name", "k"), null);
        assertEquals("stale", m.verify("ak_secret_1").code(), "never synced yet");
        m.sync();
        KeyMirror.Verification v = m.verify("ak_secret_1", List.of("read"), null);
        assertTrue(v.valid());
        assertEquals("insufficient_scope", m.verify("ak_secret_1", List.of("write"), null).code());
        assertFalse(m.verify("other").valid());
        page[0] = "{\"data\":[{\"id\":\"eak_1\",\"revoked_at\":1500}],\"has_more\":false,\"next_cursor\":\"c1\"}";
        now.set(1200);
        m.sync();
        assertFalse(m.verify("ak_secret_1").valid());
        assertEquals("c1", store.getCursor());
        now.set(5000);
        assertEquals("stale", m.verify("ak_secret_1").code());
    }

    @Test
    void localRevokeHappensBeforeAtlasAndSurvivesAnAtlasFailure() {
        List<String> deleted = new java.util.ArrayList<>();
        MemoryKeyMirrorStore store = new MemoryKeyMirrorStore();
        KeyMirror m = KeyMirror.builder(backend(new String[]{"{}"}, deleted), store).build();
        m.mint(Map.of(), null);
        m.revoke("eak_1");
        assertEquals(List.of("eak_1"), deleted);
        assertFalse(m.verify("ak_secret_1").valid());
    }

    @Test
    void ipAllowlistAndScopeHelpers() {
        assertTrue(KeyMirror.ipInAllowlist("10.1.2.3", List.of("10.0.0.0/8")));
        assertFalse(KeyMirror.ipInAllowlist("11.1.2.3", List.of("10.0.0.0/8")));
        assertTrue(KeyMirror.ipInAllowlist("2001:db8::1", List.of("2001:db8::/32")));
        assertFalse(KeyMirror.ipInAllowlist("example.com", List.of("0.0.0.0/0")));
        assertEquals(List.of("reports:read"), KeyMirror.intersectScopesWithPermissions(List.of("reports:read", "x"), List.of("reports:*")));
        assertEquals(64, KeyMirror.hashApiKeySecret("a").length());
    }
}
