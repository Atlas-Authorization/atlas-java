package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.BeforeUserCreateHook;
import net.atlasauth.atlas.models.BeforeUserCreateHookTest;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.CreateUserReferral;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ReferralBackfillBody;
import net.atlasauth.atlas.models.ReferralBackfillJob;
import net.atlasauth.atlas.models.ReferralCall;
import net.atlasauth.atlas.models.ReferralConnectionTest;
import net.atlasauth.atlas.models.ReferralRetryFailedResult;
import net.atlasauth.atlas.models.ReferralSettings;
import net.atlasauth.atlas.models.ReferralSimulateResult;
import net.atlasauth.atlas.models.UpdateBeforeUserCreateHookBody;
import net.atlasauth.atlas.models.UpdateReferralSecurityBody;
import net.atlasauth.atlas.models.UpdateReferralSettingsBody;
import net.atlasauth.atlas.models.UpdateUserBody;
import net.atlasauth.atlas.models.User;
import net.atlasauth.atlas.models.UserReferral;
import net.atlasauth.atlas.models.UserReferralFlag;
import net.atlasauth.atlas.models.UserReferralResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

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
    @Test
    void v2SettingsFieldsPingTestAndCallPaging() throws IOException {
        mock.reply(200, "{\"object\":\"referral_settings\",\"enabled\":true,\"provider\":\"referally\",\"signing_secret_set\":true,\"api_key_set\":false,"
                + "\"url_params\":[\"ref\"],\"auto_enroll_users\":false,\"tracking_token\":\"tt_public_1\",\"code_lookup_url\":null,\"signup_code_field\":true,"
                + "\"forward_lifecycle\":true,\"mirror_to_public_metadata\":false,\"include_referral_claims\":true,"
                + "\"connection\":{\"last_test\":null,\"last_ping\":{\"ok\":true,\"org_name\":\"Acme\",\"clock_skew_ms\":90000,\"clock_skew_warning\":true},"
                + "\"alert\":{\"code\":\"signature_rejected\",\"message\":\"401\",\"at\":9,\"call_id\":\"rfc_9\"},\"failed_calls_7d\":1,\"retrying_calls\":0,\"last_success_at\":null},"
                + "\"providers\":[{\"id\":\"referally\",\"capabilities\":{\"signup\":true,\"erase\":true,\"export\":true,\"partner\":true,\"auto_enroll\":true,\"signal\":true,\"alias\":true,\"user\":true,\"ping\":true},"
                + "\"needs\":[\"endpoint_url\",\"connection_id\",\"signing_secret\"]}]}");
        ReferralSettings view = atlas.referrals().get();
        assertEquals("tt_public_1", view.trackingToken());
        assertTrue(view.signupCodeField() && view.includeReferralClaims() && view.forwardLifecycle());
        assertEquals("signature_rejected", view.connection().alert().code());
        assertEquals(90000L, view.connection().lastPing().clockSkewMs());
        assertTrue(view.providers().get(0).capabilities().ping());

        mock.reply(200, SETTINGS);
        atlas.referrals().update(new UpdateReferralSettingsBody().provider("referally").trackingToken("tt_public_1")
                .forwardLifecycle(false).mirrorToPublicMetadata(true).includeReferralClaims(true).signupCodeField(true)
                .clear("code_lookup_url"));
        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("tt_public_1", body.get("tracking_token").asText());
        assertFalse(body.get("forward_lifecycle").asBoolean());
        assertTrue(body.get("mirror_to_public_metadata").asBoolean() && body.get("include_referral_claims").asBoolean()
                && body.get("signup_code_field").asBoolean());
        assertTrue(body.has("code_lookup_url") && body.get("code_lookup_url").isNull());

        mock.reply(200, "{\"ok\":true,\"http_status\":200,\"latency_ms\":31,\"method\":\"ping\",\"ping\":{\"ok\":true,\"org_name\":\"Acme\"},\"warnings\":[\"clock skew\"]}");
        ReferralConnectionTest test = atlas.referrals().test();
        assertEquals("ping", test.method());
        assertEquals("Acme", test.ping().orgName());
        assertEquals("clock skew", test.warnings().get(0));

        String call = "{\"object\":\"referral_call\",\"id\":\"rfc_2\",\"kind\":\"alias\",\"status\":\"failed\",\"customer_ref\":\"cust_1\",\"attempts\":1,"
                + "\"error_code\":\"signature_rejected\",\"latency_ms\":44,\"request_id\":\"req_1\",\"response\":{\"ok\":false},"
                + "\"highlights\":{\"matched_via\":\"code\",\"partner_external_ref\":\"cust_9\",\"notes\":[\"n\"]},\"batch_id\":\"rbf_1\",\"created_at\":1,\"updated_at\":2}";
        mock.reply(200, "{\"object\":\"list\",\"data\":[" + call + "],\"has_more\":true}");
        ListPage<ReferralCall> page = atlas.referrals().listCalls(1000, null, null, null, "signature_rejected", "rfc_0");
        assertTrue(mock.rawQuery.contains("limit=1000") && mock.rawQuery.contains("error_code=signature_rejected")
                && mock.rawQuery.contains("starting_after=rfc_0") && !mock.rawQuery.contains("status="), mock.rawQuery);
        ReferralCall got = page.data().get(0);
        assertEquals("signature_rejected", got.errorCode());
        assertEquals(44, got.latencyMs());
        assertEquals("rbf_1", got.batchId());
        assertEquals("n", got.highlights().notes().get(0));
    }

    @Test
    void v2RetryFailedSimulateBackfillAndUserResult() throws IOException {
        mock.reply(200, "{\"object\":\"referral_retry\",\"requeued\":4}");
        ReferralRetryFailedResult retried = atlas.referrals().retryFailed(null, "signature_rejected", "k1");
        assertEquals("/v1/instance/referrals/calls/retry_failed", mock.path);
        assertEquals("{\"error_code\":\"signature_rejected\"}", mock.requestBody);
        assertEquals("k1", mock.idempotencyKey);
        assertEquals(4, retried.requeued());

        mock.reply(200, "{\"object\":\"referral_simulation\",\"ok\":true,\"call_id\":\"rfc_5\",\"customer_ref\":\"atlas_test_abc\",\"http_status\":200,\"latency_ms\":20,"
                + "\"result\":{\"referral\":{\"partner_id\":\"pn_1\",\"partner_external_ref\":\"cust_9\",\"matched_via\":\"code\",\"status\":\"active\"},\"partner\":null,\"notes\":[\"matched code\"]},"
                + "\"response\":{\"referral\":{}},\"error\":null}");
        ReferralSimulateResult sim = atlas.referrals().simulate("ACME10");
        assertEquals("POST", mock.method);
        assertEquals("/v1/instance/referrals/simulate", mock.path);
        assertEquals("{\"code\":\"ACME10\"}", mock.requestBody);
        assertEquals("cust_9", sim.result().referral().partnerExternalRef());
        assertEquals("matched code", sim.result().notes().get(0));
        mock.reply(200, "{\"object\":\"referral_simulation\",\"ok\":false,\"customer_ref\":\"atlas_test_x\"}");
        atlas.referrals().simulate(null);
        assertEquals("{}", mock.requestBody);

        String job = "{\"object\":\"referral_backfill\",\"id\":\"rbf_1\",\"status\":\"running\",\"since\":1790000000000,\"code_metadata_path\":null,\"codes_given\":1,"
                + "\"users_scanned\":12,\"users_skipped\":2,\"calls_queued\":10,\"truncated\":false,\"next_since\":null,"
                + "\"calls\":{\"pending\":10,\"succeeded\":0,\"failed\":0,\"skipped\":0,\"held\":0},\"created_at\":1,\"finished_at\":null}";
        mock.reply(200, job);
        ReferralBackfillJob bf = atlas.referrals().backfill(new ReferralBackfillBody().since("2026-01-01").codes(Map.of("cust_1", "ACME10")), "k2");
        assertEquals("/v1/instance/referrals/backfill", mock.path);
        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("2026-01-01", body.get("since").asText());
        assertEquals("ACME10", body.get("codes").get("cust_1").asText());
        assertFalse(body.has("code_metadata_path"));
        assertEquals("k2", mock.idempotencyKey);
        assertEquals(10, bf.calls().pending());
        assertEquals(1790000000000L, bf.since());
        mock.reply(200, job);
        atlas.referrals().getBackfill("rbf_1");
        assertEquals("GET", mock.method);
        assertEquals("/v1/instance/referrals/backfill/rbf_1", mock.path);

        mock.reply(200, "{\"object\":\"user_referral\",\"user_id\":\"user_1\",\"status\":\"attributed\",\"customer_ref\":\"cust_1\",\"partner_id\":\"pn_1\","
                + "\"partner_external_ref\":\"cust_9\",\"referrer_user_id\":\"user_9\",\"matched_via\":\"code\",\"referral_status\":\"active\",\"notes\":[\"n\"],"
                + "\"referral_code\":\"ada-1\",\"referral_link\":null,\"context\":{\"code\":\"ACME10\"},\"risk_level\":\"low\",\"flagged\":false,\"hold_reason\":null,"
                + "\"signed_up_at\":1,\"updated_at\":2}");
        UserReferralResult res = atlas.referrals().getForUser("user_1");
        assertEquals("/v1/users/user_1/referral", mock.path);
        assertEquals("attributed", res.status());
        assertEquals("active", res.referralStatus());
        assertEquals("ACME10", res.context().get("code"));
    }

    @Test
    void v2SetExternalIdUserFieldsAndBeforeUserCreateHook() throws IOException {
        String user = "{\"object\":\"user\",\"id\":\"user_1\",\"external_id\":\"cust_1\",\"public_metadata\":{},\"mfa_enabled\":false,\"banned\":false,\"locked\":false,"
                + "\"created_at\":1,\"updated_at\":1,\"referral\":{\"status\":\"pending\",\"partner_id\":null,\"partner_external_ref\":\"cust_9\",\"referrer_external_id\":\"cust_9\","
                + "\"referrer_user_id\":null,\"matched_via\":null,\"referral_status\":\"active\",\"notes\":[\"n\"],\"risk_level\":null,\"flagged\":false}}";
        mock.reply(200, user);
        User u = atlas.users().setExternalId("user_1", "cust_1");
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/users/user_1", mock.path);
        assertEquals("{\"external_id\":\"cust_1\"}", mock.requestBody);
        assertEquals("cust_1", u.externalId());
        assertEquals("pending", u.referral().status());
        assertEquals("active", u.referral().referralStatus());
        assertEquals("cust_9", u.referral().partnerExternalRef());
        assertEquals("n", u.referral().notes().get(0));
        mock.reply(200, user);
        atlas.users().setExternalId("user_1", null);
        assertEquals("{\"external_id\":null}", mock.requestBody);
        mock.reply(200, user);
        atlas.users().update("user_1", new UpdateUserBody().externalId("cust_2"));
        assertEquals("{\"external_id\":\"cust_2\"}", mock.requestBody);
        // the 1.7 constructor still compiles
        assertEquals("lead", new UserReferral("pn_1", null, null, "code", "lead", null, false).status());

        mock.reply(200, "{\"object\":\"before_user_create_hook\",\"enabled\":true,\"url\":\"https://app.example.com/hooks/before-create\","
                + "\"signing_secret_set\":true,\"timeout_ms\":3000,\"last_call\":{\"at\":3,\"status\":\"ok\",\"error\":null},\"updated_at\":3}");
        BeforeUserCreateHook hook = atlas.userHooks().getBeforeUserCreate();
        assertEquals("GET", mock.method);
        assertEquals("/v1/instance/hooks/before_user_create", mock.path);
        assertTrue(hook.signingSecretSet());
        assertEquals("ok", hook.lastCall().status());

        mock.reply(200, "{\"object\":\"before_user_create_hook\",\"enabled\":false,\"url\":null,\"signing_secret_set\":false,\"timeout_ms\":2000}");
        atlas.userHooks().updateBeforeUserCreate(new UpdateBeforeUserCreateHookBody().enabled(true).signingSecret("s".repeat(32))
                .timeoutMs(2000).clear("url"), "k3");
        assertEquals("PATCH", mock.method);
        JsonNode body = om.readTree(mock.requestBody);
        assertTrue(body.get("enabled").asBoolean());
        assertEquals(2000, body.get("timeout_ms").asInt());
        assertTrue(body.has("url") && body.get("url").isNull());
        assertEquals("k3", mock.idempotencyKey);

        mock.reply(200, "{\"ok\":true,\"latency_ms\":12,\"answer\":{\"external_id\":\"cust_1\",\"public_metadata\":null,\"private_metadata_set\":true,\"unsafe_metadata\":null}}");
        BeforeUserCreateHookTest tested = atlas.userHooks().testBeforeUserCreate();
        assertEquals("POST", mock.method);
        assertEquals("/v1/instance/hooks/before_user_create/test", mock.path);
        assertEquals("cust_1", tested.answer().externalId());
        assertTrue(tested.answer().privateMetadataSet());
    }
}
