package net.atlasauth.atlas;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** The pre-parity public surface still works unchanged next to the added operations. */
class ParityCompatTest {
    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        mock.reply(200, "{\"object\":\"list\",\"data\":[]}");
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    @Test
    void deliveryHealthListKeepsItsOneAndTwoArgumentForms() {
        assertNotNull(atlas.delivery().health());
        assertEquals("/v1/delivery_health", mock.path);
        atlas.delivery().health("webhook_endpoint", "unhealthy");
        assertEquals("kind=webhook_endpoint&state=unhealthy", mock.rawQuery);
        mock.reply(200, "{}");
        atlas.deliveryHealth().get("webhook_endpoint", "we_1");
        assertEquals("/v1/delivery_health/webhook_endpoint/we_1", mock.path);
    }

    @Test
    void transportStringAndByteRequestsStillWork() throws Exception {
        mock.reply(200, "plain text body");
        assertEquals("plain text body", atlas.transport().getRaw("/v1/anything"));
        mock.reply(200, "{\"k\":\"v\"}");
        Map<String, Object> m = atlas.transport().get("/v1/anything", new com.fasterxml.jackson.core.type.TypeReference<>() {
        });
        assertEquals("v", m.get("k"));
        mock.reply(200, "{}");
        atlas.transport().requestBytes("POST", "/v1/anything", null, new byte[]{1}, "image/png",
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                });
        assertEquals("image/png", mock.header("content-type"));
    }
}
