package net.atlasauth.atlas;

import net.atlasauth.atlas.webhook.WebhookDedupe;
import net.atlasauth.atlas.webhook.WebhookVerifier;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebhookDedupeTest {

    @Test
    void secondDeliveryOfTheSameIdIsADuplicateAndReleaseLetsARetryThrough() throws Exception {
        String secret = "whsec_test";
        byte[] body = "{\"id\":\"evt_1\",\"type\":\"user.created\",\"data\":{}}".getBytes(StandardCharsets.UTF_8);
        String ts = String.valueOf(System.currentTimeMillis());
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String sig = java.util.Base64.getEncoder().encodeToString(mac.doFinal(("d_1." + ts + "." + new String(body, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8)));
        Map<String, String> headers = Map.of("atlas-id", "d_1", "atlas-timestamp", ts, "atlas-signature", "v1," + sig);
        WebhookDedupe d = new WebhookDedupe(new WebhookVerifier(secret), WebhookDedupe.memoryStore());
        WebhookDedupe.Result first = d.verify(body, headers);
        assertFalse(first.duplicate());
        assertTrue(d.verify(body, headers).duplicate());
        first.release();
        assertFalse(d.verify(body, headers).duplicate());
        assertThrows(WebhookDedupe.VerificationException.class, () -> d.verify(body, Map.of("atlas-id", "d_1", "atlas-timestamp", ts, "atlas-signature", "v1,00")));
    }
}
