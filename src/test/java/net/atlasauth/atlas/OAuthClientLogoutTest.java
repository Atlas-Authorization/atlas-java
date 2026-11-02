package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.CreateOAuthClientBody;
import net.atlasauth.atlas.models.OAuthClient;
import net.atlasauth.atlas.models.OAuthClientLogout;
import net.atlasauth.atlas.models.OAuthClientWithSecret;
import net.atlasauth.atlas.models.UpdateOAuthClientBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The 1.21.0 surface: client logout, refresh_touches_session, add_redirect_origins and the captcha site key. */
class OAuthClientLogoutTest {

    private static final ObjectMapper JSON = new ObjectMapper();

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
        return JSON.readTree(mock.requestBody);
    }

    @Test
    void logoutSendsTheSidAndDecodesTheResult() throws IOException {
        mock.reply(200, "{\"object\":\"oauth_client_logout\",\"client_id\":\"atlas_client_1\",\"sid\":\"sess_1\","
                + "\"session\":\"revoked\",\"grants_revoked\":2,\"backchannel_logouts\":3}");

        OAuthClientLogout out = atlas.oauthClients().logout("oac_1", "sess_1", "idem_1");

        assertEquals("POST", mock.method);
        assertEquals("/v1/oauth_clients/oac_1/logout", mock.path);
        assertEquals("sess_1", sent().get("sid").asText());
        assertEquals("idem_1", mock.header("Idempotency-Key"));
        assertEquals("revoked", out.session());
        assertEquals(2, out.grantsRevoked());
        assertEquals(3, out.backchannelLogouts());
        assertEquals("atlas_client_1", out.clientId());
    }

    @Test
    void createSendsTheNewFlagsAndReadsAddedOrigins() throws IOException {
        mock.reply(201, "{\"object\":\"oauth_client\",\"id\":\"oac_1\",\"first_party\":true,"
                + "\"refresh_touches_session\":true,\"added_origins\":[\"https://app.example.com\"],\"client_secret\":\"cs_x\"}");

        OAuthClientWithSecret c = atlas.oauthClients().create(
                new CreateOAuthClientBody("App", List.of("https://app.example.com/cb"))
                        .firstParty(true)
                        .refreshTouchesSession(true)
                        .addRedirectOrigins(true));

        assertTrue(sent().get("refresh_touches_session").asBoolean());
        assertTrue(sent().get("add_redirect_origins").asBoolean());
        assertTrue(c.refreshTouchesSession());
        assertEquals(List.of("https://app.example.com"), c.addedOrigins());
    }

    @Test
    void updateSendsTheFlagsAndOmitsThemWhenUnset() throws IOException {
        mock.reply(200, "{\"object\":\"oauth_client\",\"id\":\"oac_1\"}");

        OAuthClient c = atlas.oauthClients().update("oac_1",
                new UpdateOAuthClientBody().refreshTouchesSession(false).addRedirectOrigins(true));
        assertEquals("PATCH", mock.method);
        assertFalse(sent().get("refresh_touches_session").asBoolean(true));
        assertTrue(sent().get("add_redirect_origins").asBoolean());
        assertFalse(c.refreshTouchesSession());
        assertNull(c.addedOrigins());

        atlas.oauthClients().update("oac_1", new UpdateOAuthClientBody().name("App 2"));
        assertFalse(sent().has("refresh_touches_session"));
        assertFalse(sent().has("add_redirect_origins"));
    }

    @Test
    void captchaSecretWithASiteKeySwitchesTheProvider() throws IOException {
        mock.reply(200, "{\"object\":\"captcha_secret\",\"provider\":\"turnstile\",\"set\":true,\"switched\":true,"
                + "\"captcha\":{\"provider\":\"turnstile\",\"siteKey\":\"0x4BBB\"}}");

        JsonNode out = atlas.instanceSecurity().setCaptchaSecret("0x4AAA", "turnstile", "0x4BBB");

        assertEquals("PUT", mock.method);
        assertEquals("/v1/instance/captcha_secret", mock.path);
        assertEquals("0x4BBB", sent().get("site_key").asText());
        assertTrue(out.get("switched").asBoolean());

        atlas.instanceSecurity().setCaptchaSecret("0x4AAA", "turnstile");
        assertFalse(sent().has("site_key"));
    }

    @Test
    void earlierRecordConstructorsStillCompile() {
        assertFalse(new OAuthClient("oauth_client", "oac_1", "cid", "n", null, null, null, null, null, null,
                false, true, 1L, 2L, false, null, false, false, null, null, null, null, null, null, null, null)
                .refreshTouchesSession());
        assertNull(new OAuthClientWithSecret("oauth_client", "oac_1", "cid", "n", null, null, null, null, null, null,
                false, true, 1L, 2L, "cs_x", null, false, null, null, null, null, null, null, null, null, null)
                .addedOrigins());
    }
}
