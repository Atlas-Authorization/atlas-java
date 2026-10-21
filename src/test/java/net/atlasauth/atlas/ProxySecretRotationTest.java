package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.AtlasIdApp;
import net.atlasauth.atlas.models.AtlasIdAppWithSecret;
import net.atlasauth.atlas.models.CreateProxySecretBody;
import net.atlasauth.atlas.models.PlatformInstanceSecret;
import net.atlasauth.atlas.models.ProxySecret;
import net.atlasauth.atlas.models.ProxySecretCreated;
import net.atlasauth.atlas.models.RotateAtlasIdAppSecretBody;
import net.atlasauth.atlas.models.RotatePlatformSecretBody;
import net.atlasauth.atlas.models.RotateWebhookSecretBody;
import net.atlasauth.atlas.models.WebhookEndpoint;
import net.atlasauth.atlas.models.WebhookSecret;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The trusted reverse proxy secret, and secret rotation with an overlap for webhook endpoints,
 * per-instance platform keys and Sign in with Atlas apps.
 */
class ProxySecretRotationTest {

    private static final String VIEW = "\"object\":\"proxy_secret\",\"set\":true,\"created_at\":1800000000000,"
            + "\"secret_header\":\"X-Atlas-Proxy-Secret\",\"client_ip_header\":\"X-Atlas-Client-IP\"";

    private MockBackend mock;
    private AtlasClient atlas;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().secretKey("sk_test_123").platformKey("psk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    @Test
    void proxySecretGetCreateRotateDelete() throws IOException {
        mock.reply(200, "{" + VIEW + ",\"previous_expires_at\":null}");
        ProxySecret view = atlas.instanceSecurity().getProxySecret();
        assertEquals("GET", mock.method);
        assertEquals("/v1/instance/proxy_secret", mock.path);
        assertTrue(view.set());
        assertEquals(1_800_000_000_000L, view.createdAt());
        assertEquals("X-Atlas-Client-IP", view.clientIpHeader());

        mock.reply(201, "{" + VIEW + ",\"previous_expires_at\":null,\"secret\":\"aps_1\",\"rotated\":false}");
        ProxySecretCreated created = atlas.instanceSecurity().createProxySecret();
        assertEquals("POST", mock.method);
        assertEquals(om.readTree("{}"), om.readTree(mock.requestBody));
        assertEquals("aps_1", created.secret());

        mock.reply(200, "{" + VIEW + ",\"previous_expires_at\":1800003600000,\"secret\":\"aps_2\",\"rotated\":true}");
        ProxySecretCreated rotated = atlas.instanceSecurity().createProxySecret(new CreateProxySecretBody().overlapSeconds(3600));
        assertEquals(om.readTree("{\"overlap_seconds\":3600}"), om.readTree(mock.requestBody));
        assertTrue(rotated.rotated());
        assertEquals(1_800_003_600_000L, rotated.previousExpiresAt());

        mock.reply(200, "{\"object\":\"proxy_secret\",\"deleted\":true}");
        assertTrue(atlas.instanceSecurity().deleteProxySecret().get("deleted").asBoolean());
        assertEquals("DELETE", mock.method);
        assertEquals("/v1/instance/proxy_secret", mock.path);
    }

    @Test
    void webhookRotateSecretWithOverlap() throws IOException {
        mock.reply(200, "{\"object\":\"webhook_secret\",\"secret\":\"whsec_new\",\"overlap_seconds\":600,"
                + "\"previous_secret_expires_at\":1800000600000}");
        WebhookSecret out = atlas.webhooks().endpoints().rotateSecret("whep_1", new RotateWebhookSecretBody().overlapSeconds(600));
        assertEquals("/v1/webhook_endpoints/whep_1/rotate_secret", mock.path);
        assertEquals(om.readTree("{\"overlap_seconds\":600}"), om.readTree(mock.requestBody));
        assertEquals(600, out.overlapSeconds());
        assertEquals(1_800_000_600_000L, out.previousSecretExpiresAt());

        // The legacy constructors still build the records.
        assertNull(new WebhookSecret("webhook_secret", "whsec_x").previousSecretExpiresAt());
        assertNull(new WebhookEndpoint("webhook_endpoint", "whep_1", "https://x.example", List.of("*"), true, null, 0L, null)
                .previousSecretExpiresAt());
    }

    @Test
    void platformInstanceRotationWithOverlap() throws IOException {
        mock.reply(200, "{\"object\":\"app_secret\",\"app_id\":\"app_1\",\"instance_id\":\"ins_1\",\"secret\":\"sk_x\","
                + "\"overlap_seconds\":0,\"previous_keys_expire_at\":null,\"note\":\"n\"}");
        PlatformInstanceSecret out = atlas.platform().apps().rotateInstanceSecret("app_1", "ins_1",
                new RotatePlatformSecretBody().overlapSeconds(0), "idem_1");
        assertEquals("/v1/platform/apps/app_1/instances/ins_1/rotate_secret", mock.path);
        assertEquals(om.readTree("{\"overlap_seconds\":0}"), om.readTree(mock.requestBody));
        assertEquals("idem_1", mock.idempotencyKey);
        assertEquals("ins_1", out.instanceId());
        assertEquals("app_1", out.appId());
        assertEquals(0L, out.overlapSeconds());
        assertNull(out.previousKeysExpireAt());
    }

    @Test
    void atlasIdAppRotationsWithOverlap() throws IOException {
        mock.reply(200, "{\"object\":\"atlas_id_app\",\"id\":\"aia_1\",\"client_secret\":\"cs_new\",\"overlap_seconds\":3600,"
                + "\"previous_secret_expires_at\":1800003600000,\"secret_issued_at\":1800000000000}");
        AtlasIdAppWithSecret out = atlas.platform().atlasIdApps().rotateSecret("aia_1", new RotateAtlasIdAppSecretBody().overlapSeconds(3600));
        assertEquals("/v1/platform/atlas_id_apps/aia_1/rotate_secret", mock.path);
        assertEquals(om.readTree("{\"overlap_seconds\":3600}"), om.readTree(mock.requestBody));
        assertEquals(1_800_000_000_000L, out.secretIssuedAt());
        assertEquals(3600, out.overlapSeconds());

        mock.reply(200, "{\"object\":\"atlas_id_app\",\"id\":\"aia_1\",\"webhook_secret\":\"whsec_new\","
                + "\"webhook_previous_secret_expires_at\":1800000600000}");
        AtlasIdAppWithSecret hook = atlas.platform().atlasIdApps().rotateWebhookSecret("aia_1", new RotateAtlasIdAppSecretBody().overlapSeconds(600));
        assertEquals("/v1/platform/atlas_id_apps/aia_1/webhook/rotate_secret", mock.path);
        assertEquals(om.readTree("{\"overlap_seconds\":600}"), om.readTree(mock.requestBody));
        assertEquals(1_800_000_600_000L, hook.webhookPreviousSecretExpiresAt());

        mock.reply(200, "{\"object\":\"atlas_id_app\",\"id\":\"aia_1\",\"secret_issued_at\":1800000000000}");
        AtlasIdApp app = atlas.platform().atlasIdApps().get("aia_1");
        assertEquals(1_800_000_000_000L, app.secretIssuedAt());
    }
}
