package net.atlasauth.atlas;

import net.atlasauth.atlas.http.Resilience;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResilienceTest {

    @Test
    void retriesA503HonouringRetryAfterThenSucceeds() throws Exception {
        try (MockBackend mock = MockBackend.start()) {
            mock.responseHeaders.put("Retry-After", "2");
            mock.reply(503, "{\"errors\":[{\"code\":\"UNAVAILABLE\",\"message\":\"x\"}]}");
            List<Duration> slept = new ArrayList<>();
            AtlasClient atlas = AtlasClient.builder().secretKey("sk_test_1").apiUrl(mock.baseUrl())
                    .resilience(Resilience.builder().maxRetries(2).sleeper(d -> { slept.add(d); mock.status = 200; mock.responseBody = "{\"id\":\"usr_1\"}"; mock.responseHeaders.clear(); }).build())
                    .build();
            mock.reply(503, "{}");
            Object user = atlas.transport().get("/v1/users/usr_1", new com.fasterxml.jackson.core.type.TypeReference<com.fasterxml.jackson.databind.JsonNode>() { });
            assertEquals("usr_1", ((com.fasterxml.jackson.databind.JsonNode) user).path("id").asText());
            assertEquals(List.of(Duration.ofSeconds(2)), slept);
        }
    }

    @Test
    void doesNotRetryAWriteWithoutAnIdempotencyKeyOn502AndNeverByDefault() throws Exception {
        try (MockBackend mock = MockBackend.start()) {
            mock.reply(502, "{}");
            AtlasClient plain = AtlasClient.builder().secretKey("sk_test_1").apiUrl(mock.baseUrl()).build();
            assertThrows(AtlasApiException.class, () -> plain.transport().get("/v1/x", new com.fasterxml.jackson.core.type.TypeReference<Object>() { }));
            int[] sleeps = {0};
            AtlasClient resilient = AtlasClient.builder().secretKey("sk_test_1").apiUrl(mock.baseUrl())
                    .resilience(Resilience.builder().maxRetries(3).sleeper(d -> sleeps[0]++).build()).build();
            assertThrows(AtlasApiException.class, () -> resilient.transport().post("/v1/x", java.util.Map.of(), new com.fasterxml.jackson.core.type.TypeReference<Object>() { }));
            assertEquals(0, sleeps[0], "a POST without an idempotency key is not retried on 502");
        }
    }

    @Test
    void aLongRetryAfterIsNotWaitedOut() throws Exception {
        try (MockBackend mock = MockBackend.start()) {
            mock.responseHeaders.put("Retry-After", "3600");
            mock.reply(429, "{}");
            int[] sleeps = {0};
            AtlasClient atlas = AtlasClient.builder().secretKey("sk_test_1").apiUrl(mock.baseUrl())
                    .resilience(Resilience.builder().maxRetries(3).sleeper(d -> sleeps[0]++).build()).build();
            AtlasApiException e = assertThrows(AtlasApiException.class, () -> atlas.transport().get("/v1/x", new com.fasterxml.jackson.core.type.TypeReference<Object>() { }));
            assertEquals(Duration.ofHours(1), e.retryAfter());
            assertEquals(0, sleeps[0]);
        }
    }

    @Test
    void parsesBothRetryAfterForms() {
        assertEquals(Duration.ofSeconds(7), Resilience.parseRetryAfter("7", Instant.now()));
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        assertEquals(Duration.ofSeconds(30), Resilience.parseRetryAfter("Thu, 01 Jan 2026 00:00:30 GMT", now));
        assertNull(Resilience.parseRetryAfter("soon", now));
    }
}
