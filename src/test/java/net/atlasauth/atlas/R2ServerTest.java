package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.AtlasIdAppManagedBy;
import net.atlasauth.atlas.models.AtlasIdAppPreflight;
import net.atlasauth.atlas.models.DryRunResult;
import net.atlasauth.atlas.models.WebhookSecret;
import net.atlasauth.atlas.http.Json;
import net.atlasauth.atlas.verify.OidcClaims;
import net.atlasauth.atlas.verify.SessionClaims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Round-2 server surface: adoption, instance app preflight, dry-run fields, sealed recipient, SSO claims. */
class R2ServerTest {
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

    @Test
    void setManagedBySendsInstanceIdAndNull() {
        mock.reply(200, "{\"object\":\"atlas_id_app\",\"id\":\"app_1\",\"managed_via\":\"adopted\",\"changed\":true}");
        AtlasIdAppManagedBy r = atlas.platform().atlasIdApps().setManagedBy("app_1", "ins_1");
        assertEquals("PUT", mock.method);
        assertEquals("/v1/platform/atlas_id_apps/app_1/managed_by", mock.path);
        assertEquals("{\"instance_id\":\"ins_1\"}", mock.requestBody);
        assertEquals("adopted", r.managedVia());
        assertTrue(r.changed());
        atlas.platform().atlasIdApps().setManagedBy("app_1", null);
        assertEquals("{\"instance_id\":null}", mock.requestBody);
    }

    @Test
    void instancePreflightDecodesTheNewFields() {
        mock.reply(200, "{\"object\":\"preflight\",\"app_id\":\"app_1\",\"ok\":true,\"checked_at\":1,\"user_count\":2,\"findings\":[],"
                + "\"button_rendered_origins\":[{\"origin\":\"https://a.example\",\"last_seen_at\":1,\"requests\":2}],"
                + "\"authorize_requests_7d\":{\"button\":1,\"js\":0,\"fedcm\":0,\"fallback\":2,\"unmarked\":0},"
                + "\"fallback_origins\":[],\"homepage_includes_script\":false}");
        AtlasIdAppPreflight p = atlas.atlasIdGrants().preflight("app_1");
        assertEquals("GET", mock.method);
        assertEquals("/v1/atlas_id_apps/app_1/preflight", mock.path);
        assertEquals("https://a.example", p.buttonRenderedOrigins().get(0).origin());
        assertEquals(2, p.authorizeRequests7d().fallback());
        assertFalse(p.homepageIncludesScript());
    }

    @Test
    void secretRecipientHeaderRidesEveryCall() {
        mock.reply(200, "{}");
        AtlasClient sealed = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).secretRecipient("x25519:abc").build();
        sealed.whoami();
        assertEquals("x25519:abc", mock.header("x-secret-recipient"));
        atlas.whoami();
        assertNull(mock.header("x-secret-recipient"));
        atlas.withSecretRecipient("x25519:zzz").whoami();
        assertEquals("x25519:zzz", mock.header("x-secret-recipient"));
    }

    @Test
    void sealedAndDryRunFieldsDecode() throws Exception {
        ObjectMapper om = Json.mapper();
        WebhookSecret w = om.readValue("{\"object\":\"webhook_secret\",\"sealed_secret\":\"AAAA\",\"secrets_sealed\":{\"algorithm\":\"x25519-sealed-box\",\"recipient\":\"x25519:k\",\"fields\":[\"secret\"]}}", WebhookSecret.class);
        assertEquals("AAAA", w.sealedSecret());
        assertEquals("secret", w.secretsSealed().fields().get(0));
        DryRunResult d = om.readValue("{\"object\":\"dry_run\",\"dry_run\":true,\"mode\":\"rollback\",\"validated\":true,\"changes\":[],\"warnings\":[{\"code\":\"origin_still_referenced\"}],\"would_return_status\":201,\"suppressed\":[\"email\"],\"validation_skipped\":{\"reason\":\"external_call\",\"detail\":\"d\"},\"note\":\"\"}", DryRunResult.class);
        assertEquals("rollback", d.mode());
        assertEquals(201, d.wouldReturnStatus());
        assertEquals("email", d.suppressed().get(0));
        assertEquals("external_call", d.validationSkipped().reason());
    }

    @Test
    void ssoClaims() {
        Map<String, Object> claims = Map.of("sub", "u", "sso_connection_id", "sso_1", "sso_organization_id", "org_1", "sso_connection_type", "saml");
        SessionClaims s = new SessionClaims(claims);
        assertEquals("sso_1", s.ssoConnectionId());
        assertEquals("org_1", s.ssoOrganizationId());
        assertEquals("saml", s.ssoConnectionType());
        OidcClaims.SsoContext sso = OidcClaims.ssoOf(claims).orElseThrow();
        assertEquals("org_1", sso.organizationId());
        assertEquals("saml", sso.type());
        assertTrue(OidcClaims.ssoOf(Map.of("sub", "u")).isEmpty());
        assertNull(OidcClaims.ssoOf(Map.of("sso_connection_id", "s", "sso_connection_type", "ldap")).orElseThrow().type());
        assertEquals("https://x.example/authorize?client_id=c&organization=acme",
                OidcClaims.authorizeUrlWithOrganization("https://x.example/authorize?client_id=c&organization=old", "acme"));
    }
}
