package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.ApiKeyImport;
import net.atlasauth.atlas.models.AppEvent;
import net.atlasauth.atlas.models.BrowserTicket;
import net.atlasauth.atlas.models.CreateApiKeyBody;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.InstanceUpdateResult;
import net.atlasauth.atlas.models.PublishAppEventBody;
import net.atlasauth.atlas.models.TestingSignInUrl;
import net.atlasauth.atlas.models.UpdateHostedPagesBody;
import net.atlasauth.atlas.models.UpdateInstanceBody;
import net.atlasauth.atlas.models.User;
import net.atlasauth.atlas.webhook.WebhookVerification;
import net.atlasauth.atlas.webhook.WebhookVerifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The 2026-10-30 round: application webhooks, Standard Webhooks, browser tickets, instance settings, user fields. */
class AppEventsTest {
    private static final ObjectMapper OM = new ObjectMapper();
    private static final byte[] KEY = "app-webhook-signing-key-32bytes!".getBytes(StandardCharsets.UTF_8);
    private static final String SECRET = "whsec_" + Base64.getEncoder().encodeToString(KEY);
    private static final long NOW = 1_761_782_400L;
    private static final byte[] BODY = "{\"type\":\"call.completed\",\"data\":{\"call_id\":\"c1\"}}".getBytes(StandardCharsets.UTF_8);

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private JsonNode sent() throws IOException {
        return OM.readTree(mock.requestBody);
    }

    @Test
    void publishSendsTheBodyAndIdempotencyKeyAndDecodesTheReplay() throws IOException {
        mock.reply(201, "{\"object\":\"app_event\",\"id\":\"evt_1\",\"type\":\"call.completed\",\"organization_id\":\"org_1\","
                + "\"data\":{\"call_id\":\"c1\"},\"created_at\":1,\"idempotent_replay\":false,\"deliveries_queued\":2,\"endpoint_ids\":[\"we_1\",\"we_2\"]}");
        AppEvent e = atlas.appEvents().publish(new PublishAppEventBody("org_1", "call.completed", Map.of("call_id", "c1")), "c1");
        assertEquals("POST", mock.method);
        assertEquals("/v1/app_events", mock.path);
        assertEquals("c1", mock.idempotencyKey);
        assertEquals("org_1", sent().get("organization_id").asText());
        assertEquals("c1", sent().get("data").get("call_id").asText());
        assertEquals(Boolean.FALSE, e.idempotentReplay());
        assertEquals(2, e.deliveriesQueued());
        assertEquals(List.of("we_1", "we_2"), e.endpointIds());
    }

    @Test
    void listCarriesTheOrganizationAndThePickerIsOrgLevel() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[],\"has_more\":false}");
        atlas.appEvents().list("org_1");
        assertEquals("/v1/app_events", mock.path);
        assertEquals("organization_id=org_1", mock.rawQuery);
        atlas.appEvents().endpoints().appEventTypes("org_1");
        assertEquals("/v1/organizations/org_1/app_event_types", mock.path);
        atlas.appEvents().endpoints().test("org_1", "we_1", "10s");
        assertEquals("/v1/organizations/org_1/app_webhook_endpoints/we_1/test", mock.path);
        assertEquals("wait=10s", mock.rawQuery);
        atlas.appEvents().types().get("call.completed");
        assertEquals("/v1/app_event_types/call.completed", mock.path);
    }

    @Test
    void browserTicketAndTestingSignInUrl() throws IOException {
        mock.reply(201, "{\"object\":\"browser_ticket\",\"id\":\"sit_1\",\"user_id\":\"user_1\",\"ticket\":\"t\",\"url\":null,"
                + "\"redirect_url\":null,\"expires_in\":60,\"expires_at\":\"2026-10-30T00:01:00Z\"}");
        BrowserTicket b = atlas.sessions().createBrowserTicket("user_1");
        assertEquals("/v1/sessions", mock.path);
        assertEquals(OM.readTree("{\"user_id\":\"user_1\",\"mode\":\"browser_ticket\"}"), sent());
        assertEquals(60, b.expiresIn());

        mock.reply(201, "{\"object\":\"testing_sign_in_url\",\"url\":\"https://app.example.com/x\",\"expires_in\":120}");
        TestingSignInUrl u = atlas.testing().createSignInUrl("user_1", "https://app.example.com/", 120L);
        assertEquals("/v1/testing/sign_in_url", mock.path);
        assertEquals(120, sent().get("expires_in_seconds").asInt());
        assertEquals("https://app.example.com/x", u.url());
    }

    @Test
    void hostedPagesClearSendsNullAndInstanceWarningsDecode() throws IOException {
        mock.reply(200, "{\"object\":\"hosted_pages\",\"sign_in_url\":null}");
        atlas.instance().hostedPages().update(new UpdateHostedPagesBody().signUpUrl("https://app.example.com/join").clearSignInUrl());
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/instance/hosted_pages", mock.path);
        assertTrue(sent().has("sign_in_url") && sent().get("sign_in_url").isNull());
        assertEquals("https://app.example.com/join", sent().get("sign_up_url").asText());

        mock.reply(200, "{\"object\":\"instance\",\"id\":\"ins_1\",\"warnings\":[{\"code\":\"unknown_field\",\"message\":\"m\","
                + "\"field\":\"auth_config.session.foo\",\"did_you_mean\":\"auth_config.session.absoluteMs\"}]}");
        InstanceUpdateResult r = atlas.instance().update(new UpdateInstanceBody());
        assertEquals("unknown_field", r.warnings().get(0).code());
        assertEquals("auth_config.session.absoluteMs", r.warnings().get(0).didYouMean());
    }

    @Test
    void userImageUrlImportKeyAndExternalAccounts() throws IOException {
        mock.reply(200, "{\"object\":\"user\",\"id\":\"user_1\",\"external_accounts\":[{\"object\":\"external_account\","
                + "\"id\":\"eac_1\",\"provider\":\"google\",\"provider_user_id\":\"g1\",\"email_verified\":true,\"connected_at\":5}]}");
        User u = atlas.users().create(new CreateUserBody("a@example.com").imageUrl("https://cdn.example.com/a.png"));
        assertEquals("https://cdn.example.com/a.png", sent().get("image_url").asText());
        assertEquals("google", u.externalAccounts().get(0).provider());
        assertTrue(u.externalAccounts().get(0).emailVerified());

        mock.reply(201, "{\"object\":\"api_key\",\"id\":\"ak_1\"}");
        atlas.apiKeys().create(new CreateApiKeyBody("user", "user_1").importKey(new ApiKeyImport("abc", "acme")));
        assertEquals("abc", sent().get("import").get("secret_hash").asText());
        assertFalse(sent().has("import_key"));
    }

    // ---- Standard Webhooks ------------------------------------------------

    private static String sig(String id, long ts, byte[] body, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        mac.update((id + "." + ts + ".").getBytes(StandardCharsets.UTF_8));
        mac.update(body);
        return "v1," + Base64.getEncoder().encodeToString(mac.doFinal());
    }

    private static Map<String, String> headers(String id, Object ts, String signature) {
        Map<String, String> h = new HashMap<>();
        h.put("Webhook-Id", id);
        h.put("Webhook-Timestamp", String.valueOf(ts));
        if (signature != null) h.put("Webhook-Signature", signature);
        return h;
    }

    private static WebhookVerification verify(Map<String, String> h, byte[] body) {
        return WebhookVerifier.verifyStandard(SECRET, body, h, NOW, 300);
    }

    @Test
    void standardVerifiesASignedDelivery() throws Exception {
        long ts = NOW - 10;
        WebhookVerification r = verify(headers("msg_1", ts, sig("msg_1", ts, BODY, KEY)), BODY);
        assertTrue(r.verified(), String.valueOf(r.reason()));
        assertEquals("msg_1", r.deliveryId());
        assertEquals(ts, r.timestamp());
        assertEquals("call.completed", r.event().get("type").asText());
    }

    @Test
    void standardWebhooksPublishedVector() {
        Map<String, String> h = headers("msg_p5jXN8AQM9LWM0D4loKWxJek", 1_614_265_330L, "v1,g0hM9SsE+OTPJTGt/tmIKtSyZlE3uFJELVlNIOLJ1OE=");
        WebhookVerification r = WebhookVerifier.verifyStandard("whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw",
                "{\"test\": 2432232314}".getBytes(StandardCharsets.UTF_8), h, 1_614_265_330L, 300);
        assertTrue(r.verified(), String.valueOf(r.reason()));
    }

    @Test
    void standardAcceptsARotatedSecondSignature() throws Exception {
        String both = sig("msg_1", NOW, BODY, "old-key".getBytes(StandardCharsets.UTF_8)) + " " + sig("msg_1", NOW, BODY, KEY);
        assertTrue(verify(headers("msg_1", NOW, both), BODY).verified());
    }

    @Test
    void standardRejections() throws Exception {
        byte[] wrong = "wrong-key".getBytes(StandardCharsets.UTF_8);
        assertEquals("signature_mismatch", verify(headers("msg_1", NOW, sig("msg_1", NOW, BODY, wrong)), BODY).reason());
        byte[] tampered = new String(BODY, StandardCharsets.UTF_8).replace("c1", "c2").getBytes(StandardCharsets.UTF_8);
        assertEquals("signature_mismatch", verify(headers("msg_1", NOW, sig("msg_1", NOW, BODY, KEY)), tampered).reason());
        assertEquals("stale_timestamp", verify(headers("msg_1", NOW - 301, sig("msg_1", NOW - 301, BODY, KEY)), BODY).reason());
        assertEquals("stale_timestamp", verify(headers("msg_1", NOW + ".5", sig("msg_1", NOW, BODY, KEY)), BODY).reason());
        assertEquals("missing_headers", verify(headers("msg_1", NOW, null), BODY).reason());
        assertEquals("malformed_signature", verify(headers("msg_1", NOW, "v2,abc v1,"), BODY).reason());
    }
}
