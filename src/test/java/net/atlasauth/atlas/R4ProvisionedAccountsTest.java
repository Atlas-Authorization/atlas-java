package net.atlasauth.atlas;

import net.atlasauth.atlas.models.AccountDomain;
import net.atlasauth.atlas.models.AtlasIdProvisionedAccount;
import net.atlasauth.atlas.models.CreateAtlasIdProvisionedAccountBody;
import net.atlasauth.atlas.models.RotateAtlasIdProvisionedPasswordBody;
import net.atlasauth.atlas.models.UpdateAtlasIdAppBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test and review accounts, account domains, the test-mode app fields and the new error codes. */
class R4ProvisionedAccountsTest {
    private static final String ACCOUNT = "{\"object\":\"atlas_id_provisioned_account\",\"id\":\"pa_1\",\"sub\":\"s_1\",\"email\":\"rev@example.com\","
            + "\"email_verified\":true,\"label\":\"reviewer\",\"kind\":\"provisioned\",\"provisioned_by\":\"app\",\"app_ids\":[\"aia_1\"],"
            + "\"apps\":[{\"id\":\"aia_1\",\"client_id\":\"c1\",\"name\":\"App\"}],\"any_app\":false,\"two_factor_exempt\":false,"
            + "\"expires_at\":null,\"expired\":false,\"developer_account_id\":null,\"created_by\":{\"type\":\"platform_key\",\"id\":\"k1\"},"
            + "\"last_sign_in_at\":null,\"password_rotated_at\":null,\"created_at\":1,\"updated_at\":2";

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().platformKey("psk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    @Test
    void provisionedAccountRoutes() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[" + ACCOUNT + "}],\"limit\":25,\"total\":1}");
        var list = atlas.platform().atlasIdApps().provisionedAccounts().list("aia_1");
        assertEquals("GET", mock.method);
        assertEquals("/v1/platform/atlas_id_apps/aia_1/provisioned_accounts", mock.path);
        assertEquals(25, list.limit());
        assertEquals("rev@example.com", list.data().get(0).email());
        assertEquals("c1", list.data().get(0).apps().get(0).clientId());

        mock.reply(201, ACCOUNT + ",\"password\":\"Sup3r-secret-pw\"}");
        AtlasIdProvisionedAccount a = atlas.platform().atlasIdApps().provisionedAccounts().create("aia_1",
                new CreateAtlasIdProvisionedAccountBody().email("rev@example.com").label("reviewer").password("Sup3r-secret-pw").twoFactorExempt(true));
        assertEquals("POST", mock.method);
        assertTrue(mock.requestBody.contains("\"email\":\"rev@example.com\""));
        assertTrue(mock.requestBody.contains("\"two_factor_exempt\":true"));
        assertEquals("Sup3r-secret-pw", a.password());
        assertNull(mock.header("x-secret-recipient"));

        mock.reply(200, ACCOUNT + "}");
        assertEquals("pa_1", atlas.platform().atlasIdApps().provisionedAccounts().get("aia_1", "pa_1").id());
        assertEquals("/v1/platform/atlas_id_apps/aia_1/provisioned_accounts/pa_1", mock.path);

        mock.reply(200, "{\"object\":\"atlas_id_provisioned_account\",\"id\":\"pa_1\",\"deleted\":true,\"sessions_ended\":2}");
        var gone = atlas.platform().atlasIdApps().provisionedAccounts().remove("aia_1", "pa_1");
        assertEquals("DELETE", mock.method);
        assertEquals(2, gone.sessionsEnded());

        mock.reply(200, ACCOUNT + ",\"password\":\"Generated-pw-123\"}");
        var rotated = atlas.platform().atlasIdApps().provisionedAccounts().rotatePassword("aia_1", "pa_1");
        assertEquals("POST", mock.method);
        assertEquals("/v1/platform/atlas_id_apps/aia_1/provisioned_accounts/pa_1/password", mock.path);
        assertEquals("Generated-pw-123", rotated.password());
    }

    @Test
    void sealedPasswordPathAndNoPlaintextInOutput() {
        mock.reply(201, ACCOUNT + ",\"sealed_password\":\"sealed:abc\"}");
        AtlasIdProvisionedAccount a = atlas.platform().atlasIdApps().provisionedAccounts().create("aia_1",
                new CreateAtlasIdProvisionedAccountBody().email("rev@example.com").label("reviewer"), null, "x25519:abc");
        assertEquals("x25519:abc", mock.header("x-secret-recipient"));
        assertEquals("sealed:abc", a.sealedPassword());
        assertNull(a.password());
        assertFalse(a.toString().contains("sealed:abc"));

        mock.reply(200, ACCOUNT + ",\"sealed_password\":\"sealed:def\"}");
        atlas.platform().atlasIdApps().provisionedAccounts().rotatePassword("aia_1", "pa_1", new RotateAtlasIdProvisionedPasswordBody(), "x25519:zzz");
        assertEquals("x25519:zzz", mock.header("x-secret-recipient"));
    }

    @Test
    void passwordNeverPrinted() {
        var body = new CreateAtlasIdProvisionedAccountBody().email("r@example.com").label("l").password("Plain-text-pw-1");
        assertFalse(body.toString().contains("Plain-text-pw-1"));
        assertFalse(new RotateAtlasIdProvisionedPasswordBody().password("Plain-text-pw-2").toString().contains("Plain-text-pw-2"));
        mock.reply(201, ACCOUNT + ",\"password\":\"Plain-text-pw-3\"}");
        var a = atlas.platform().atlasIdApps().provisionedAccounts().create("aia_1", body);
        assertFalse(a.toString().contains("Plain-text-pw-3"));
    }

    @Test
    void domainRoutes() {
        String dom = "{\"object\":\"account_domain\",\"id\":\"dom_1\",\"domain\":\"example.com\",\"verified\":false,\"verified_at\":null,"
                + "\"last_checked_at\":null,\"verification\":{\"type\":\"TXT\",\"name\":\"_atlas.example.com\",\"value\":\"v1\"},\"created_at\":1}";
        mock.reply(200, "{\"object\":\"list\",\"data\":[" + dom + "],\"verified_domains\":[\"example.com\"]}");
        var list = atlas.platform().domains().list();
        assertEquals("/v1/platform/domains", mock.path);
        assertEquals("example.com", list.verifiedDomains().get(0));

        mock.reply(201, dom);
        AccountDomain d = atlas.platform().domains().claim("example.com");
        assertEquals("POST", mock.method);
        assertTrue(mock.requestBody.contains("\"domain\":\"example.com\""));
        assertEquals("TXT", d.verification().type());

        mock.reply(200, dom);
        atlas.platform().domains().check("dom_1");
        assertEquals("POST", mock.method);
        assertEquals("/v1/platform/domains/dom_1/verify", mock.path);
        atlas.platform().domains().verify("dom_1");
        assertEquals("/v1/platform/domains/dom_1/verify", mock.path);

        mock.reply(200, "{}");
        atlas.platform().domains().remove("dom_1");
        assertEquals("DELETE", mock.method);
        assertEquals("/v1/platform/domains/dom_1", mock.path);
    }

    @Test
    void testModeFieldsAndErrorCodes() {
        mock.reply(200, "{\"object\":\"atlas_id_app\",\"id\":\"aia_1\",\"test_mode\":true,\"provisioned_verified_domains_only\":true}");
        var app = atlas.platform().atlasIdApps().update("aia_1", new UpdateAtlasIdAppBody().testMode(true).provisionedVerifiedDomainsOnly(true));
        assertEquals("PATCH", mock.method);
        assertTrue(mock.requestBody.contains("\"test_mode\":true"));
        assertTrue(mock.requestBody.contains("\"provisioned_verified_domains_only\":true"));
        assertEquals(Boolean.TRUE, app.testMode());
        assertEquals(Boolean.TRUE, app.provisionedVerifiedDomainsOnly());

        assertEquals("TEST_ACCOUNT_RESTRICTED", ErrorCodes.TEST_ACCOUNT_RESTRICTED);
        assertEquals("PROVISIONED_ACCOUNT_LIMIT_REACHED", ErrorCodes.PROVISIONED_ACCOUNT_LIMIT_REACHED);
        assertEquals("DOMAIN_NOT_VERIFIED", ErrorCodes.DOMAIN_NOT_VERIFIED);
    }
}
