package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.BeforeUserCreateHook;
import net.atlasauth.atlas.models.CreateAppInvitationBody;
import net.atlasauth.atlas.models.CreateOAuthClientBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ListPlatformAppsParams;
import net.atlasauth.atlas.models.MessagingTestResult;
import net.atlasauth.atlas.models.OAuthClient;
import net.atlasauth.atlas.models.OAuthClientSecretReveal;
import net.atlasauth.atlas.models.OAuthClientWithSecret;
import net.atlasauth.atlas.models.Preflight;
import net.atlasauth.atlas.models.PreflightAck;
import net.atlasauth.atlas.models.UpdateBeforeUserCreateHookBody;
import net.atlasauth.atlas.models.UpdateOAuthClientBody;
import net.atlasauth.atlas.models.User;
import net.atlasauth.atlas.models.WebhookTestResult;
import net.atlasauth.atlas.webhook.WebhookEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The rest of 1.21.0: preflight acks, secret reveal, require_verified_email, invitation expiry, app filters and new fields. */
class PreflightAcksAndSecretRevealTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().secretKey("sk_test_123").platformKey("psk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private JsonNode sent() throws IOException {
        return JSON.readTree(mock.requestBody);
    }

    private static final String ACK = "{\"object\":\"preflight_ack\",\"code\":\"pkce_not_required\",\"reason\":\"legacy app\","
            + "\"by\":\"ak_1\",\"at\":1000,\"expires_at\":null,\"active\":true}";

    @Test
    void preflightAcksListSetAndDelete() throws IOException {
        mock.reply(200, ACK);
        PreflightAck a = atlas.instance().acknowledgePreflight("pkce_not_required", "legacy app", 1893456000000L);
        assertEquals("PUT", mock.method);
        assertEquals("/v1/instance/preflight/acks/pkce_not_required", mock.path);
        assertEquals("legacy app", sent().get("reason").asText());
        assertEquals(1893456000000L, sent().get("expires_at").asLong());
        assertTrue(a.active());
        assertNull(a.expiresAt());

        atlas.instance().acknowledgePreflight("pkce_not_required", "legacy app", null);
        assertFalse(sent().has("expires_at"));

        mock.reply(200, "{\"object\":\"list\",\"data\":[" + ACK + "],\"has_more\":false}");
        ListPage<PreflightAck> list = atlas.instance().listPreflightAcks();
        assertEquals("GET", mock.method);
        assertEquals("/v1/instance/preflight/acks", mock.path);
        assertEquals("ak_1", list.data().get(0).by());

        mock.reply(200, "{\"object\":\"preflight_ack\",\"code\":\"pkce_not_required\",\"deleted\":true}");
        assertTrue(atlas.instance().deletePreflightAck("pkce_not_required").get("deleted").asBoolean());
        assertEquals("DELETE", mock.method);
    }

    @Test
    void preflightFindingCarriesAcknowledged() {
        mock.reply(200, "{\"object\":\"preflight\",\"ok\":true,\"findings\":[{\"severity\":\"warning\",\"code\":\"pkce_not_required\","
                + "\"message\":\"m\",\"fix\":\"f\",\"acknowledged\":{\"reason\":\"legacy app\",\"by\":null,\"at\":5,\"expires_at\":9}}]}");
        Preflight p = atlas.instance().preflight();
        assertEquals("legacy app", p.findings().get(0).acknowledged().reason());
        assertEquals(9L, p.findings().get(0).acknowledged().expiresAt());
    }

    @Test
    void revealSecretAndCreateWithChosenSecret() throws IOException {
        mock.reply(200, "{\"object\":\"oauth_client_secret\",\"id\":\"oac_1\",\"client_id\":\"cid\",\"client_secret\":\"cs_x\","
                + "\"secret_prefix\":\"cs_\",\"client_secret_expires_at\":null,\"note\":\"n\"}");
        OAuthClientSecretReveal r = atlas.oauthClients().revealSecret("oac_1");
        assertEquals("POST", mock.method);
        assertEquals("/v1/oauth_clients/oac_1/secret/reveal", mock.path);
        assertEquals("cs_x", r.clientSecret());

        mock.reply(201, "{\"object\":\"oauth_client\",\"id\":\"oac_1\",\"client_secret\":\"cs_y\",\"require_verified_email\":true,"
                + "\"client_secret_reveal\":{\"path\":\"/v1/oauth_clients/oac_1/secret/reveal\",\"expires_at\":600000}}");
        OAuthClientWithSecret c = atlas.oauthClients().create(new CreateOAuthClientBody("App", List.of("https://app.example.com/cb"))
                .clientSecret("s".repeat(40)).requireVerifiedEmail(true));
        assertEquals("s".repeat(40), sent().get("client_secret").asText());
        assertTrue(sent().get("require_verified_email").asBoolean());
        assertTrue(c.requireVerifiedEmail());
        assertEquals("/v1/oauth_clients/oac_1/secret/reveal", c.clientSecretReveal().path());
        assertEquals(600000L, c.clientSecretReveal().expiresAt());

        mock.reply(200, "{\"object\":\"oauth_client\",\"id\":\"oac_1\",\"require_verified_email\":false}");
        OAuthClient u = atlas.oauthClients().update("oac_1", new UpdateOAuthClientBody().requireVerifiedEmail(false));
        assertFalse(sent().get("require_verified_email").asBoolean(true));
        assertFalse(u.requireVerifiedEmail());
    }

    @Test
    void invitationExpiryAndPlatformAppFilters() throws IOException {
        mock.reply(201, "{\"object\":\"invitation\",\"id\":\"inv_1\"}");
        atlas.invitations().create(new CreateAppInvitationBody("a@example.com").expiresInDays(30));
        assertEquals(30, sent().get("expires_in_days").asInt());
        atlas.invitations().create(new CreateAppInvitationBody("a@example.com").expiresAt("2027-01-01T00:00:00Z"));
        assertEquals("2027-01-01T00:00:00Z", sent().get("expires_at").asText());

        mock.reply(200, "{\"data\":[],\"has_more\":false}");
        atlas.platform().apps().list(new ListPlatformAppsParams().name("Acme").namePrefix("Ac").externalRef("acme/prod").limit(10).startingAfter("ins_9"));
        assertTrue(mock.rawQuery.contains("name=Acme"));
        assertTrue(mock.rawQuery.contains("name_prefix=Ac"));
        assertTrue(mock.rawQuery.contains("external_ref=acme%2Fprod") || mock.rawQuery.contains("external_ref=acme/prod"));
        assertTrue(mock.rawQuery.contains("limit=10"));
        assertTrue(mock.rawQuery.contains("starting_after=ins_9"));
    }

    @Test
    void hookFailClosedAndNewReadFields() throws IOException {
        mock.reply(200, "{\"object\":\"before_user_create_hook\",\"enabled\":true,\"url\":\"https://h.example.com\","
                + "\"signing_secret_set\":true,\"timeout_ms\":3000,\"fail_closed\":true}");
        BeforeUserCreateHook h = atlas.userHooks().updateBeforeUserCreate(new UpdateBeforeUserCreateHookBody().failClosed(true));
        assertTrue(sent().get("fail_closed").asBoolean());
        assertTrue(h.failClosed());

        mock.reply(200, "{\"object\":\"user\",\"id\":\"user_1\",\"status\":\"suspended\",\"created_at\":1,\"updated_at\":2}");
        User user = atlas.users().get("user_1");
        assertEquals("suspended", user.status());

        mock.reply(200, "{\"object\":\"webhook_test_result\",\"delivery_id\":\"d\",\"event_id\":\"e\",\"url\":\"https://h.example.com/wh\","
                + "\"status\":\"success\",\"will_retry\":false}");
        WebhookTestResult w = atlas.webhooks().endpoints().testNow("we_1", null);
        assertEquals("https://h.example.com/wh", w.url());

        mock.reply(200, "{\"object\":\"messaging_test\",\"channel\":\"email\",\"sent_to\":\"a@example.com\",\"ok\":true,"
                + "\"accepted\":true,\"provider\":\"resend\",\"provider_message_id\":\"re_1\"}");
        MessagingTestResult m = atlas.messaging().test("email", "a@example.com");
        assertTrue(m.accepted());
        assertEquals("resend", m.provider());
        assertEquals("re_1", m.providerMessageId());
        assertNull(m.providerResponse());

        assertEquals("session.expired", WebhookEvents.SESSION_EXPIRED);
        assertEquals("invitation.accepted", WebhookEvents.INVITATION_ACCEPTED);
    }
}
