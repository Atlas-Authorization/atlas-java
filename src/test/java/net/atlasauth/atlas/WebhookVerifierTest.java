package net.atlasauth.atlas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.atlasauth.atlas.webhook.WebhookVerification;
import net.atlasauth.atlas.webhook.WebhookVerifier;
import org.junit.jupiter.api.Test;

class WebhookVerifierTest {
    private static final String SECRET = "whsec_test_secret";
    private static final long NOW = 1_757_937_600_000L;
    private static final String BODY = "{\"id\":\"evt_1\",\"type\":\"user.updated\",\"data\":{\"id\":\"user_1\"}}";

    private static Map<String, Object> sign(String id, long ts, String body, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String sig = Base64.getEncoder().encodeToString(
                mac.doFinal((id + "." + ts + "." + body).getBytes(StandardCharsets.UTF_8)));
        Map<String, Object> h = new HashMap<>();
        h.put("Atlas-Id", id);
        h.put("Atlas-Timestamp", Long.toString(ts));
        h.put("Atlas-Signature", List.of("v1," + sig));
        return h;
    }

    private static WebhookVerification verify(String body, Map<String, Object> headers) {
        return new WebhookVerifier(SECRET).verify(body.getBytes(StandardCharsets.UTF_8), headers, NOW);
    }

    @Test
    void returnsTheDeliveryIdAndTimestamp() throws Exception {
        WebhookVerification r = verify(BODY, sign("evt_1", NOW - 1000, BODY, SECRET));
        assertTrue(r.verified());
        assertEquals("evt_1", r.deliveryId());
        assertEquals(NOW - 1000, r.timestamp());
        assertEquals("user.updated", r.event().get("type").asText());
    }

    @Test
    void rejections() throws Exception {
        WebhookVerification tampered = verify(BODY.replace("user_1", "user_2"), sign("evt_1", NOW, BODY, SECRET));
        assertFalse(tampered.verified());
        assertEquals("signature_mismatch", tampered.reason());
        assertNull(tampered.deliveryId());
        assertEquals("signature_mismatch", verify(BODY, sign("evt_1", NOW, BODY, "whsec_x")).reason());
        assertEquals("stale_timestamp", verify(BODY, sign("evt_1", NOW - 360_000, BODY, SECRET)).reason());
        assertEquals("stale_timestamp", verify(BODY, sign("evt_1", NOW + 360_000, BODY, SECRET)).reason());
        assertEquals("missing_headers", verify(BODY, new HashMap<>()).reason());
        Map<String, Object> bad = sign("evt_1", NOW, BODY, SECRET);
        bad.put("Atlas-Signature", "v2,abc");
        assertEquals("malformed_signature", verify(BODY, bad).reason());
    }
}
