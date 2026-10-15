package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.CreateOAuthClientBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OAuthClient;
import net.atlasauth.atlas.models.OAuthClientWithSecret;
import net.atlasauth.atlas.models.UpdateOAuthClientBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** OAuth client {@code skip_logout_confirmation}: request bodies and response decoding. */
class OAuthClientsTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private static final String CLIENT = "{\"object\":\"oauth_client\",\"id\":\"oac_1\",\"client_id\":\"cid_1\","
            + "\"name\":\"Portal\",\"redirect_uris\":[\"https://app.test/cb\"],\"is_public\":false,"
            + "\"first_party\":true,\"created_at\":1,\"updated_at\":2,\"skip_logout_confirmation\":true";

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
    void createSendsSkipLogoutConfirmation() throws IOException {
        mock.reply(201, CLIENT + ",\"client_secret\":\"cs_x\"}");

        OAuthClientWithSecret c = atlas.oauthClients().create(
                new CreateOAuthClientBody("Portal", List.of("https://app.test/cb"))
                        .firstParty(true)
                        .skipLogoutConfirmation(true));

        assertEquals("POST", mock.method);
        assertEquals("/v1/oauth_clients", mock.path);
        assertTrue(sent().get("skip_logout_confirmation").asBoolean());
        assertTrue(c.skipLogoutConfirmation());
        assertEquals("cs_x", c.clientSecret());
    }

    @Test
    void updateSendsSkipLogoutConfirmationAndOmitsItWhenUnset() throws IOException {
        mock.reply(200, CLIENT + "}");

        OAuthClient c = atlas.oauthClients().update("oac_1", new UpdateOAuthClientBody().skipLogoutConfirmation(false));

        assertEquals("PATCH", mock.method);
        assertEquals("/v1/oauth_clients/oac_1", mock.path);
        assertFalse(sent().get("skip_logout_confirmation").asBoolean(true));
        assertTrue(c.skipLogoutConfirmation());

        atlas.oauthClients().update("oac_1", new UpdateOAuthClientBody().name("Portal 2"));
        assertFalse(sent().has("skip_logout_confirmation"));
        atlas.oauthClients().create(new CreateOAuthClientBody("Portal", List.of("https://app.test/cb")));
        assertFalse(sent().has("skip_logout_confirmation"));
    }

    @Test
    void readsDefaultToFalseWhenTheFieldIsAbsent() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[{\"object\":\"oauth_client\",\"id\":\"oac_2\"}],"
                + "\"has_more\":false}");
        ListPage<OAuthClient> page = atlas.oauthClients().list();
        assertFalse(page.data().get(0).skipLogoutConfirmation());

        mock.reply(200, CLIENT + "}");
        assertTrue(atlas.oauthClients().get("oac_1").skipLogoutConfirmation());
        assertEquals("/v1/oauth_clients/oac_1", mock.path);
    }

    @Test
    void thirdPartyClientIsRefused() {
        mock.reply(422, "{\"errors\":[{\"code\":\"VALIDATION_FAILED\",\"message\":\"first-party only\","
                + "\"param\":\"skip_logout_confirmation\"}]}");
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> atlas.oauthClients().update("oac_1", new UpdateOAuthClientBody().skipLogoutConfirmation(true)));
        assertTrue(ex.hasCode(ErrorCodes.VALIDATION_FAILED));
        assertEquals("skip_logout_confirmation", ex.errors().get(0).param());
    }

    @Test
    void legacyRecordConstructorsStillCompile() {
        assertFalse(new OAuthClient("oauth_client", "oac_1", "cid", "n", null, null, null, null, null, null,
                false, true, 1L, 2L).skipLogoutConfirmation());
        assertFalse(new OAuthClientWithSecret("oauth_client", "oac_1", "cid", "n", null, null, null, null, null, null,
                false, true, 1L, 2L, "cs_x", null).skipLogoutConfirmation());
    }
}
