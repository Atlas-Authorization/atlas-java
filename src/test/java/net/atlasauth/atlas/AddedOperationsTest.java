package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.AtlasIdPublisherBody;
import net.atlasauth.atlas.models.CookieConsent;
import net.atlasauth.atlas.models.ForumulateIntegrationBody;
import net.atlasauth.atlas.models.LogoutNotifyEndpointBody;
import net.atlasauth.atlas.models.ManagedEmailBody;
import net.atlasauth.atlas.models.OriginMove;
import net.atlasauth.atlas.models.OriginMoveBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Request shaping and decoding for the origin move, connections, integrations, logout fan-out, managed email, back-channel logs and the Sign in with Atlas publisher. */
class AddedOperationsTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        mock.reply(200, "{\"object\":\"x\",\"data\":[],\"changes\":[],\"problems\":[]}");
        atlas = AtlasClient.builder()
                .secretKey("sk_test_123")
                .platformKey("psk_test_456")
                .apiUrl(mock.baseUrl())
                .build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private void expect(String method, String path, String query, String body, Consumer<AtlasClient> call) throws IOException {
        mock.requestBody = null;
        call.accept(atlas);
        assertEquals(method, mock.method, path);
        assertEquals(path, mock.path);
        assertEquals(query, mock.rawQuery, path + " query");
        if (body == null) {
            assertTrue(mock.requestBody == null || mock.requestBody.isEmpty(), path + " should send no body");
        } else {
            assertEquals(JSON.readTree(body), JSON.readTree(mock.requestBody), path + " body");
        }
    }

    @Test
    void addedOperationsHitTheirRoutes() throws IOException {
        OriginMoveBody move = new OriginMoveBody().from("https://old.example.com").to("https://new.example.com").dryRun(true);
        String moveJson = "{\"from\":\"https://old.example.com\",\"to\":\"https://new.example.com\",\"dry_run\":true}";
        expect("POST", "/v1/instance/move_origin", null, moveJson, c -> c.instance().moveOrigin(move));
        expect("GET", "/v1/instance/connections", null, null, c -> c.instance().connections());
        expect("GET", "/v1/instance/integrations/chatly", null, null, c -> c.instance().getChatlyIntegration());
        expect("PUT", "/v1/instance/integrations/chatly", null, "{\"channel\":\"support\"}", c -> c.instance().setChatlyIntegration("support"));
        expect("DELETE", "/v1/instance/integrations/chatly", null, null, c -> c.instance().deleteChatlyIntegration());
        expect("GET", "/v1/instance/integrations/forumulate", null, null, c -> c.instance().getForumulateIntegration());
        expect("PUT", "/v1/instance/integrations/forumulate", null, "{\"org_id\":\"org_1\",\"spaces\":[\"sp_1\"]}",
                c -> c.instance().setForumulateIntegration(new ForumulateIntegrationBody().orgId("org_1").spaces(List.of("sp_1"))));
        expect("DELETE", "/v1/instance/integrations/forumulate", null, null, c -> c.instance().deleteForumulateIntegration());
        expect("GET", "/v1/logout_notify_endpoints", null, null, c -> c.logoutNotifyEndpoints().list());
        expect("POST", "/v1/logout_notify_endpoints", null, "{\"url\":\"https://hooks.example.com/logout\"}",
                c -> c.logoutNotifyEndpoints().create(new LogoutNotifyEndpointBody().url("https://hooks.example.com/logout")));
        expect("GET", "/v1/logout_notify_endpoints/lne_1", null, null, c -> c.logoutNotifyEndpoints().get("lne_1"));
        expect("PATCH", "/v1/logout_notify_endpoints/lne_1", null, "{\"description\":\"x\"}",
                c -> c.logoutNotifyEndpoints().update("lne_1", new LogoutNotifyEndpointBody().description("x")));
        expect("DELETE", "/v1/logout_notify_endpoints/lne_1", null, null, c -> c.logoutNotifyEndpoints().delete("lne_1"));
        expect("GET", "/v1/logout_notify_endpoints/lne_1/deliveries", "limit=5", null, c -> c.logoutNotifyEndpoints().listDeliveries("lne_1", 5));
        expect("GET", "/v1/messaging_providers/email/managed", null, null, c -> c.messaging().getManagedEmail());
        expect("POST", "/v1/messaging_providers/email/managed", null, "{\"from_address\":\"no-reply@example.com\"}",
                c -> c.messaging().createManagedEmail(new ManagedEmailBody().fromAddress("no-reply@example.com")));
        expect("POST", "/v1/messaging_providers/email/managed/verify", null, null, c -> c.messaging().verifyManagedEmail());
        expect("DELETE", "/v1/messaging_providers/email/managed", "force=true", null, c -> c.messaging().deleteManagedEmail(true));
        expect("GET", "/v1/oauth_clients/oac_1/backchannel_deliveries", "limit=10", null, c -> c.oauthClients().listBackchannelDeliveries("oac_1", 10, null));
        expect("POST", "/v1/oauth_clients/oac_1/backchannel_logout/test", null, null, c -> c.oauthClients().testBackchannelLogout("oac_1"));
        expect("GET", "/v1/platform/atlas_id_apps/app_1/backchannel_deliveries", "starting_after=d_1", null,
                c -> c.platform().atlasIdApps().listBackchannelDeliveries("app_1", null, "d_1"));
        expect("POST", "/v1/platform/atlas_id_apps/app_1/backchannel_logout/test", null, null, c -> c.platform().atlasIdApps().testBackchannelLogout("app_1"));
        expect("GET", "/v1/platform/atlas_id_apps/app_1/preflight", null, null, c -> c.platform().atlasIdApps().preflight("app_1"));
        expect("GET", "/v1/platform/atlas_id_apps/app_1/users", "email=a%40example.com", null, c -> c.platform().atlasIdApps().findUsers("app_1", "a@example.com"));
        expect("DELETE", "/v1/platform/atlas_id_apps/app_1/grants/gr_1", null, null, c -> c.platform().atlasIdApps().revokeGrant("app_1", "gr_1"));
        expect("POST", "/v1/platform/atlas_id_apps/app_1/move_origin", null, moveJson, c -> c.platform().atlasIdApps().moveOrigin("app_1", move));
        expect("POST", "/v1/platform/atlas_id_apps/app_1/webhook/rotate_secret", null, null, c -> c.platform().atlasIdApps().rotateWebhookSecret("app_1"));
        expect("GET", "/v1/platform/atlas_id_publisher", null, null, c -> c.platform().atlasIdPublisher().get());
        expect("PUT", "/v1/platform/atlas_id_publisher", null, "{\"name\":\"Acme\",\"domain\":\"example.com\"}",
                c -> c.platform().atlasIdPublisher().set(new AtlasIdPublisherBody().name("Acme").domain("example.com")));
        expect("PATCH", "/v1/platform/atlas_id_publisher", null, "{\"skip_consent\":true}", c -> c.platform().atlasIdPublisher().setSkipConsent(true));
        expect("DELETE", "/v1/platform/atlas_id_publisher", null, null, c -> c.platform().atlasIdPublisher().delete());
        expect("POST", "/v1/platform/atlas_id_publisher/verify", null, null, c -> c.platform().atlasIdPublisher().verify());
        expect("GET", "/v1/platform/config_changelog", null, null, c -> c.platform().configChangelog());
    }

    @Test
    void decodesOriginMoveAndCookieInventory() {
        mock.reply(200, "{\"object\":\"origin_move\",\"from\":\"https://old.example.com\",\"to\":\"https://new.example.com\",\"dry_run\":false,\"applied\":true,"
                + "\"changes\":[{\"object\":\"oauth_client\",\"id\":\"oac_1\",\"field\":\"redirect_uris\",\"before\":[\"https://old.example.com/cb\"],\"after\":[\"https://new.example.com/cb\"]}],\"problems\":[]}");
        OriginMove m = atlas.instance().moveOrigin(new OriginMoveBody().from("https://old.example.com").to("https://new.example.com"));
        assertTrue(m.applied());
        JsonNode after = m.changes().get(0).after();
        assertEquals("https://new.example.com/cb", after.get(0).asText());

        mock.reply(200, "{\"object\":\"cookie_consent\",\"enabled\":true,\"culture\":\"page\",\"declared_cookies\":[{\"name\":\"__session\",\"pattern\":null,\"store\":\"cookie\","
                + "\"domain\":\".example.com\",\"lifetime_seconds\":null,\"lifetime\":\"session\",\"http_only\":true,\"category\":\"necessary\",\"purpose\":\"p\",\"written_when\":\"w\",\"set_by\":\"atlas_frontend_api\"}]}");
        CookieConsent cc = atlas.instance().getCookieConsent();
        assertEquals("page", cc.culture());
        assertEquals("cookie", cc.declaredCookies().get(0).store());
        assertTrue(cc.declaredCookies().get(0).httpOnly());
    }
}
