package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.CreateUserReferral;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ReferralCall;
import net.atlasauth.atlas.models.ReferralConnectionTest;
import net.atlasauth.atlas.models.ReferralSettings;
import net.atlasauth.atlas.models.UpdateReferralSecurityBody;
import net.atlasauth.atlas.models.UpdateReferralSettingsBody;
import net.atlasauth.atlas.models.UpdateUserBody;
import net.atlasauth.atlas.models.User;
import net.atlasauth.atlas.models.UserReferralFlag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Referral attribution: settings, connection test, call log, user referral fields and the fraud flag. */
class ReferralsTest {

    private static final String SETTINGS = "{\"object\":\"referral_settings\",\"enabled\":true,\"provider\":\"webhook\","
            + "\"endpoint_url\":\"https://referrals.example.com/atlas\",\"program_id\":null,\"connection_id\":null,"
            + "\"signing_secret_set\":true,\"api_key_set\":false,\"url_params\":[\"ref\",\"via\",\"aff\"],\"context_cookie\":null,"
            + "\"consent_category\":\"referrals\",\"consent_fallback_category\":\"marketing\",\"auto_enroll_users\":true,"
            + "\"link_template\":\"https://app.example.com/join?ref={code}\",\"timeout_ms\":3000,"
            + "\"security\":{\"challenge_referred_signups\":\"always\",\"on_high_risk\":\"flag\",\"hold_until_email_verified\":false,\"max_referred_signups_per_ip_hour\":5},"
            + "\"missing\":[],\"connection\":{\"last_test\":{\"ok\":true,\"at\":1,\"http_status\":404,\"error\":null},\"failed_calls_7d\":2,\"retrying_calls\":1,\"last_success_at\":1},"
            + "\"providers\":[{\"id\":\"webhook\",\"name\":\"Atlas referral protocol (any platform)\",\"capabilities\":{\"signup\":true,\"erase\":true,\"export\":true,\"partner\":true,\"auto_enroll\":true,\"signal\":true},"
            + "\"needs\":[\"endpoint_url\",\"signing_secret\"],\"default_context_cookie\":null,\"docs_url\":null}],\"updated_at\":1}";
    private static final String CALL = "{\"object\":\"referral_call\",\"id\":\"rfc_1\",\"kind\":\"signup\",\"status\":\"succeeded\",\"outcome\":\"referred\","
            + "\"customer_ref\":\"user_1\",\"user_id\":\"user_1\",\"http_status\":200,\"last_error\":null,\"attempts\":2,\"next_attempt_at\":null,"
            + "\"risk\":{\"level\":\"low\",\"score\":5,\"signals\":[],\"captcha\":\"passed\",\"emailVerified\":true},\"payload\":{\"customerRef\":\"user_1\"},"
            + "\"created_at\":1,\"updated_at\":2}";

    private MockBackend mock;
    private AtlasClient atlas;
    private final ObjectMapper om = new ObjectMapper();

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
    void settingsGetUpdateAndTest() throws IOException {
        mock.reply(200, SETTINGS);
        ReferralSettings view = atlas.referrals().get();
        assertEquals("GET", mock.method);
        assertEquals("/v1/instance/referrals", mock.path);
        assertEquals("flag", view.security().onHighRisk());
        assertEquals(5, view.security().maxReferredSignupsPerIpHour());
        assertTrue(view.providers().get(0).capabilities().signal());
        assertEquals(2, view.connection().failedCalls7d());
        assertEquals(404, view.connection().lastTest().httpStatus());

        mock.reply(200, SETTINGS);
        atlas.referrals().update(new UpdateReferralSettingsBody().enabled(true).signingSecret("x".repeat(32))
                .security(new UpdateReferralSecurityBody().onHighRisk("hold")).clear("link_template"), "idem_1");
        assertEquals("PATCH", mock.method);
        JsonNode body = om.readTree(mock.requestBody);
        assertTrue(body.get("enabled").asBoolean());
        assertEquals("hold", body.get("security").get("on_high_risk").asText());
        assertEquals(1, body.get("security").size());
        assertTrue(body.has("link_template") && body.get("link_template").isNull());
        assertFalse(body.has("endpoint_url"));
        assertEquals("idem_1", mock.idempotencyKey);

        mock.reply(200, "{\"ok\":false,\"http_status\":401,\"latency_ms\":12,\"error\":{\"code\":\"signature_rejected\",\"message\":\"m\"}}");
        ReferralConnectionTest test = atlas.referrals().test();
        assertEquals("/v1/instance/referrals/test", mock.path);
        assertFalse(test.ok());
        assertEquals("signature_rejected", test.error().code());
    }

    @Test
    void callLogAndRetry() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[" + CALL + "],\"has_more\":false}");
        ListPage<ReferralCall> page = atlas.referrals().listCalls(10, "failed", "signup", "user_1");
        assertEquals("/v1/instance/referrals/calls", mock.path);
        assertTrue(mock.rawQuery.contains("limit=10") && mock.rawQuery.contains("status=failed")
                && mock.rawQuery.contains("kind=signup") && mock.rawQuery.contains("user_id=user_1"), mock.rawQuery);
        assertTrue(page.data().get(0).risk().emailVerified());

        mock.reply(200, CALL);
        ReferralCall call = atlas.referrals().retryCall("rfc_1", "idem_2");
        assertEquals("POST", mock.method);
        assertEquals("/v1/instance/referrals/calls/rfc_1/retry", mock.path);
        assertEquals("idem_2", mock.idempotencyKey);
        assertEquals("referred", call.outcome());
    }

    @Test
    void userReferralFieldsAndFlag() throws IOException {
        String user = "{\"object\":\"user\",\"id\":\"user_1\",\"public_metadata\":{},\"mfa_enabled\":false,\"banned\":false,\"locked\":false,"
                + "\"created_at\":1,\"updated_at\":1,\"referral\":{\"partner_id\":\"pn_1\",\"referrer_external_id\":\"cust_9\",\"referrer_user_id\":\"user_9\","
                + "\"matched_via\":\"code\",\"status\":\"lead\",\"risk_level\":\"low\",\"flagged\":false},\"referral_code\":\"ada-1\","
                + "\"referral_link\":\"https://app.example.com/join?ref=ada-1\"}";
        mock.reply(200, user);
        User created = atlas.users().create(new CreateUserBody("a@example.com")
                .referral(new CreateUserReferral().code("ACME10").clickId("clk_123")).referralCode("ada-1"), null);
        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("ACME10", body.get("referral").get("code").asText());
        assertEquals("clk_123", body.get("referral").get("click_id").asText());
        assertEquals("ada-1", body.get("referral_code").asText());
        assertEquals("user_9", created.referral().referrerUserId());
        assertEquals("ada-1", created.referralCode());

        mock.reply(200, user);
        atlas.users().update("user_1", new UpdateUserBody().clearReferralCode());
        assertEquals("{\"referral_code\":null}", mock.requestBody);
        mock.reply(200, user);
        atlas.users().update("user_1", new UpdateUserBody().referralCode("ada-2"));
        assertEquals("{\"referral_code\":\"ada-2\"}", mock.requestBody);

        mock.reply(200, "{\"object\":\"user_referral\",\"user_id\":\"user_1\",\"status\":\"flagged\",\"flagged\":true,\"flag_reason\":\"ring\",\"flagged_at\":5,\"risk_level\":\"high\"}");
        UserReferralFlag flag = atlas.users().flagReferral("user_1", "ring", "idem_3");
        assertEquals("/v1/users/user_1/referral/flag", mock.path);
        assertEquals("{\"reason\":\"ring\"}", mock.requestBody);
        assertEquals("idem_3", mock.idempotencyKey);
        assertTrue(flag.flagged());
        assertEquals(5L, flag.flaggedAt());
        atlas.users().flagReferral("user_1");
        assertEquals("{}", mock.requestBody);
    }

    @Test
    void referralCodeTaken() {
        mock.reply(409, "{\"errors\":[{\"code\":\"REFERRAL_CODE_TAKEN\",\"message\":\"taken\",\"param\":\"referral_code\"}]}");
        AtlasApiException e = assertThrows(AtlasApiException.class,
                () -> atlas.users().update("user_1", new UpdateUserBody().referralCode("ada-1")));
        assertEquals(409, e.status());
        assertEquals(ErrorCodes.REFERRAL_CODE_TAKEN, e.code());
        assertEquals("REFERRALS_NOT_CONFIGURED", ErrorCodes.REFERRALS_NOT_CONFIGURED);
    }
}
