package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class SidRevocationListTest {

    @Test
    void rejectsListedSidsUntilTheTokenCouldNotBeLiveAndFailsClosedWhenStale() throws Exception {
        AtomicLong now = new AtomicLong(1_000_000);
        boolean[] down = {false};
        JsonNode page = new ObjectMapper().readTree("{\"data\":[{\"sid\":\"s1\",\"revoked_at\":1000000}],\"has_more\":false,\"server_time\":1000000}");
        Backend b = new Backend() {
            public JsonNode createApiKey(Map<String, Object> body, String k) { return null; }
            public void deleteApiKey(String id) { }
            public JsonNode listRevokedApiKeys(String s, int l) { return null; }
            @Override
            public JsonNode listRevokedSessions(Long since, String cursor, int limit) {
                if (down[0]) {
                    throw new IllegalStateException("atlas down");
                }
                return page;
            }
        };
        SidRevocationList list = SidRevocationList.builder(b).maxTokenLifetimeSeconds(60).intervalMs(1000).rejectWhenStale(true, 5000).clock(now::get).build();
        assertTrue(list.isRevoked("s1"));
        assertFalse(list.isRevoked("s2"));
        assertFalse(list.unavailable());
        down[0] = true;
        now.addAndGet(70_000); // past the 60 s token lifetime
        assertTrue(list.isRevoked("s1") == false, "expired after the token lifetime");
        assertTrue(list.unavailable(), "no successful poll for longer than staleAfterMs");
    }
}
