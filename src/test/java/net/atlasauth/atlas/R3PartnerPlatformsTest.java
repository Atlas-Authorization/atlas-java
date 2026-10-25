package net.atlasauth.atlas;

import net.atlasauth.atlas.http.Json;
import net.atlasauth.atlas.models.PartnerPlatform;
import net.atlasauth.atlas.models.SetIntegrationBody;
import net.atlasauth.atlas.models.SetPartnerPlatformBody;
import net.atlasauth.atlas.models.SetPrivacyOrchestratorBody;
import net.atlasauth.atlas.models.StorePartnerPlatformTokenBody;
import net.atlasauth.atlas.models.UserSearchParams;
import net.atlasauth.atlas.operator.OperatorLogin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Round-3 server surface: partner platforms, per-call audience, orchestrator fields, include_deleted, operator audiences. */
class R3PartnerPlatformsTest {
    private static final String PLATFORM = "{\"object\":\"partner_platform\",\"id\":\"pp_1\",\"partner\":\"acme\",\"name\":\"Acme\",\"preset\":false,"
            + "\"platform_id\":\"plat_1\",\"enabled\":true,\"jwks_url\":\"https://p.example/jwks\",\"signature_header\":\"x-sig\",\"signature_scheme\":\"t_kid_ed25519\","
            + "\"tolerance_seconds\":300,\"allowed_sources\":[\"a\"],\"record_consents\":true,\"receiver_url\":\"https://r.example\",\"receiver_url_template\":\"\","
            + "\"serves_deployment\":false,\"registration\":{\"status\":\"registered\",\"registered_at\":5},\"token_set\":true,\"token_prefix\":\"pt_ab\","
            + "\"last_delivery_at\":null,\"last_delivery_error\":null,\"created_at\":1,\"updated_at\":2}";

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

    @Test
    void partnerPlatformRoutes() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[" + PLATFORM + "],\"has_more\":false,\"presets\":[{\"id\":\"x\"}]}");
        var list = atlas.partnerPlatforms().list();
        assertEquals("GET", mock.method);
        assertEquals("/v1/partner_platforms", mock.path);
        assertEquals("acme", list.data().get(0).partner());
        assertEquals(1, list.presets().size());

        mock.reply(200, PLATFORM);
        PartnerPlatform p = atlas.partnerPlatforms().get("acme");
        assertEquals("/v1/partner_platforms/acme", mock.path);
        assertEquals("registered", p.registration().status());
        assertTrue(p.tokenSet());

        atlas.partnerPlatforms().set("acme", new SetPartnerPlatformBody().jwksUrl("https://p.example/jwks").recordConsents(true).clear("name"));
        assertEquals("PUT", mock.method);
        assertTrue(mock.requestBody.contains("\"jwks_url\":\"https://p.example/jwks\""));
        assertTrue(mock.requestBody.contains("\"record_consents\":true"));
        assertTrue(mock.requestBody.contains("\"name\":null"));

        mock.reply(200, "{\"object\":\"partner_platform\",\"partner\":\"acme\",\"deleted\":true}");
        assertTrue(atlas.partnerPlatforms().remove("acme").deleted());
        assertEquals("DELETE", mock.method);

        mock.reply(200, "{\"object\":\"partner_platform_registration\",\"partner\":\"acme\",\"platform_id\":\"plat_1\",\"receiver_url\":\"https://r\",\"receiver_url_is_template\":false,"
                + "\"external_id\":null,\"events\":[\"consent.recorded\"],\"signature\":{\"header\":\"h\",\"scheme\":\"s\",\"format\":\"f\",\"jwks_url\":\"j\",\"tolerance_seconds\":300},"
                + "\"send_to\":{\"contact\":\"c\",\"docs\":\"d\"},\"token_prefix\":null,\"next_step\":\"n\"}");
        var reg = atlas.partnerPlatforms().registration("acme");
        assertEquals("/v1/partner_platforms/acme/registration", mock.path);
        assertEquals(300, reg.signature().toleranceSeconds());
        assertEquals("c", reg.sendTo().contact());

        mock.reply(200, "{\"object\":\"list\",\"data\":[{\"object\":\"partner_delivery\",\"event_id\":\"e\",\"event_type\":\"t\",\"outcome\":\"recorded\",\"user_id\":\"u\",\"received_at\":9}],\"has_more\":false}");
        var d = atlas.partnerPlatforms().deliveries("acme", 5);
        assertEquals("/v1/partner_platforms/acme/deliveries", mock.path);
        assertEquals("limit=5", mock.rawQuery);
        assertEquals("recorded", d.data().get(0).outcome());
    }

    @Test
    void tokenIsSentButNeverInAResponseModelOrToString() throws Exception {
        String secret = "pt_live_SUPERSECRETVALUE";
        mock.reply(200, PLATFORM);
        StorePartnerPlatformTokenBody body = new StorePartnerPlatformTokenBody().token(secret).rotate(true).apiBaseUrl("https://p.example");
        PartnerPlatform p = atlas.partnerPlatforms().storeToken("acme", body);
        assertEquals("POST", mock.method);
        assertEquals("/v1/partner_platforms/acme/token", mock.path);
        assertTrue(mock.requestBody.contains(secret));
        assertTrue(mock.requestBody.contains("\"api_base_url\":\"https://p.example\""));
        assertFalse(body.toString().contains(secret));
        assertFalse(p.toString().contains(secret));
        assertFalse(Json.mapper().writeValueAsString(p).contains(secret));
        assertEquals("pt_ab", p.tokenPrefix());
    }

    @Test
    void perCallAudienceOnTheIntegrationAndTheMint() {
        atlas.instance().setIntegration("acme", new SetIntegrationBody()
                .perCallAudience(Map.of("param", "site", "template", "acme:{site}", "allowed", List.of("s1"), "any_matching", false)));
        assertTrue(mock.requestBody.contains("\"per_call_audience\":{"));
        assertTrue(mock.requestBody.contains("\"template\":\"acme:{site}\""));
        atlas.instance().setIntegration("acme", new SetIntegrationBody().clear("per_call_audience"));
        assertTrue(mock.requestBody.contains("\"per_call_audience\":null"));

        atlas.sessions().getIntegrationToken("sess_1", "acme", null, Map.of("site", "s1"));
        assertEquals("POST", mock.method);
        assertEquals("/v1/sessions/sess_1/integration_tokens/acme", mock.path);
        assertEquals("site=s1", mock.rawQuery);
        atlas.sessions().getIntegrationToken("sess_1", "acme", "acme:s2", null);
        assertEquals("audience=acme%3As2", mock.rawQuery);
        atlas.sessions().getIntegrationToken("sess_1", "acme");
        assertNull(mock.rawQuery);
    }

    @Test
    void orchestratorFieldsAndIncludeDeleted() {
        atlas.privacy().orchestrator().set(new SetPrivacyOrchestratorBody().siteId("s1").partnerLinkId("pl_1").cancelDeletionOnSignIn(true));
        assertTrue(mock.requestBody.contains("\"site_id\":\"s1\""));
        assertTrue(mock.requestBody.contains("\"partner_link_id\":\"pl_1\""));
        assertTrue(mock.requestBody.contains("\"cancel_deletion_on_sign_in\":true"));

        mock.reply(200, "{\"object\":\"list\",\"data\":[],\"has_more\":false}");
        atlas.users().list(new UserSearchParams().includeDeleted(true));
        assertEquals("include_deleted=true", mock.rawQuery);
    }

    @Test
    void operatorAudiencesAndErrorCode() throws Exception {
        var c = Json.mapper().readValue("{\"issuer\":\"i\",\"token_exchange_audience\":\"atlas\",\"audiences\":[\"a\"],\"access_token_claims\":[\"sub\"]}", OperatorLogin.OperatorConfiguration.class);
        assertEquals("a", c.audiences().get(0));
        assertEquals("sub", c.accessTokenClaims().get(0));
        assertEquals("MANAGED_WAF_NOT_CONFIGURED", ErrorCodes.MANAGED_WAF_NOT_CONFIGURED);
    }
}
