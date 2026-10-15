package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import net.atlasauth.atlas.models.ApiKey;
import net.atlasauth.atlas.models.ApiKeyLimit;
import net.atlasauth.atlas.models.ApiKeyUsage;
import net.atlasauth.atlas.models.ApiKeyUsageDebit;
import net.atlasauth.atlas.models.ApiKeyVerification;
import net.atlasauth.atlas.models.ApiKeyWebhookData;
import net.atlasauth.atlas.models.ApiKeyWithSecret;
import net.atlasauth.atlas.models.CreateApiKeyBody;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.ImportExportImportUsersBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.RecordApiKeyUsageBody;
import net.atlasauth.atlas.models.Session;
import net.atlasauth.atlas.models.SessionTouch;
import net.atlasauth.atlas.models.UpdateApiKeyBody;
import net.atlasauth.atlas.models.UserSession;
import net.atlasauth.atlas.models.WebhookPayload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** API key spend caps and usage metering, session keep-alive, and the notify flag. */
class ApiKeyLimitsTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private static final String STATE = "{\"unit\":\"requests\",\"period\":\"day\",\"amount\":1000,\"used\":10,"
            + "\"remaining\":990,\"window_start\":1760400000000,\"resets_at\":1760486400000}";
    private static final String LIMITS = "[{\"unit\":\"requests\",\"amount\":1000,\"period\":\"day\"},"
            + "{\"unit\":\"usd_cents\",\"amount\":500,\"period\":\"request\"}]";

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
    void createSendsLimitsAndDecodesUsage() throws IOException {
        mock.reply(201, "{\"object\":\"api_key\",\"id\":\"ak_1\",\"secret\":\"ak_s\",\"created_at\":1,"
                + "\"limits\":" + LIMITS + ",\"usage\":[" + STATE + "]}");

        ApiKeyWithSecret key = atlas.apiKeys().create(new CreateApiKeyBody("user", "user_1").limits(List.of(
                new ApiKeyLimit("requests", 1000, ApiKeyLimit.PERIOD_DAY),
                new ApiKeyLimit("usd_cents", 500, ApiKeyLimit.PERIOD_REQUEST))));

        assertEquals("POST", mock.method);
        assertEquals("/v1/api_keys", mock.path);
        assertEquals(JSON.readTree(LIMITS), sent().get("limits"));
        assertEquals(2, key.limits().size());
        assertEquals(990, key.usage().get(0).remaining());
        assertEquals(1760486400000L, key.usage().get(0).resetsAt());
    }

    @Test
    void getDecodesLimitsAndAKeyWithoutCapsHasNullLimits() {
        mock.reply(200, "{\"object\":\"api_key\",\"id\":\"ak_1\",\"created_at\":1,\"limits\":null,\"usage\":[]}");
        ApiKey key = atlas.apiKeys().get("ak_1");
        assertNull(key.limits());
        assertTrue(key.usage().isEmpty());
    }

    @Test
    void updateCanReplaceOrClearLimits() throws IOException {
        mock.reply(200, "{\"object\":\"api_key\",\"id\":\"ak_1\",\"created_at\":1}");
        atlas.apiKeys().update("ak_1", new UpdateApiKeyBody().limits(List.of(new ApiKeyLimit("tokens", 5, "total"))));
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/api_keys/ak_1", mock.path);
        assertEquals(JSON.readTree("{\"limits\":[{\"unit\":\"tokens\",\"amount\":5,\"period\":\"total\"}]}"), sent());

        mock.reply(200, "{\"object\":\"api_key\",\"id\":\"ak_1\",\"created_at\":1}");
        atlas.apiKeys().update("ak_1", new UpdateApiKeyBody().clearLimits());
        assertEquals(JSON.readTree("{\"limits\":null}"), sent());
    }

    @Test
    void recordUsageSendsAmountUnitAndBodyIdempotencyKey() throws IOException {
        mock.reply(200, "{\"object\":\"api_key_usage_debit\",\"key_id\":\"ak_1\",\"unit\":\"requests\",\"amount\":1,"
                + "\"allowed\":true,\"exceeded\":[],\"usage\":[" + STATE + "],"
                + "\"counters\":[{\"unit\":\"requests\",\"period\":\"day\",\"used\":11,\"window_start\":1,\"resets_at\":2},"
                + "{\"unit\":\"requests\",\"period\":\"total\",\"used\":11,\"window_start\":0,\"resets_at\":null}],"
                + "\"idempotent_replay\":false}");

        ApiKeyUsageDebit debit = atlas.apiKeys().recordUsage("ak_1",
                new RecordApiKeyUsageBody(1, "requests").idempotencyKey("req_42"));

        assertEquals("POST", mock.method);
        assertEquals("/v1/api_keys/ak_1/usage", mock.path);
        assertEquals(JSON.readTree("{\"amount\":1,\"unit\":\"requests\",\"idempotency_key\":\"req_42\"}"), sent());
        assertNull(mock.idempotencyKey, "the idempotency key travels in the body");
        assertTrue(debit.allowed());
        assertNull(debit.code());
        assertEquals("ak_1", debit.keyId());
        assertEquals(11, debit.counters().get(0).used());
        assertNull(debit.counters().get(1).resetsAt());
        assertFalse(debit.idempotentReplay());
    }

    @Test
    void recordUsageRefusalIsAValueNotAnException() throws IOException {
        mock.reply(200, "{\"object\":\"api_key_usage_debit\",\"key_id\":\"ak_1\",\"unit\":\"usd_cents\",\"amount\":900,"
                + "\"allowed\":false,\"code\":\"LIMIT_EXCEEDED\",\"exceeded\":[" + STATE + "],\"usage\":[" + STATE + "],"
                + "\"counters\":[],\"idempotent_replay\":true}");

        ApiKeyUsageDebit debit = atlas.apiKeys().recordUsage("ak_1", 900, "usd_cents");

        assertEquals(JSON.readTree("{\"amount\":900,\"unit\":\"usd_cents\"}"), sent());
        assertFalse(debit.allowed());
        assertEquals(ErrorCodes.LIMIT_EXCEEDED, debit.code());
        assertEquals(1, debit.exceeded().size());
        assertTrue(debit.idempotentReplay());
    }

    @Test
    void recordUsageIdempotencyConflictThrows() {
        mock.reply(409, "{\"errors\":[{\"code\":\"IDEMPOTENCY_CONFLICT\",\"message\":\"reused\"}]}");
        ConflictException ex = assertThrows(ConflictException.class,
                () -> atlas.apiKeys().recordUsage("ak_1", 2, "requests"));
        assertTrue(ex.hasCode(ErrorCodes.IDEMPOTENCY_CONFLICT));
    }

    @Test
    void getUsage() {
        mock.reply(200, "{\"object\":\"api_key_usage\",\"key_id\":\"ak_1\",\"limits\":" + LIMITS
                + ",\"usage\":[" + STATE + "],\"counters\":[]}");

        ApiKeyUsage usage = atlas.apiKeys().getUsage("ak_1");

        assertEquals("GET", mock.method);
        assertEquals("/v1/api_keys/ak_1/usage", mock.path);
        assertEquals("api_key_usage", usage.object());
        assertEquals("usd_cents", usage.limits().get(1).unit());
        assertEquals(500, usage.limits().get(1).amount());
        assertEquals("request", usage.limits().get(1).period());
        assertEquals(10, usage.usage().get(0).used());
    }

    @Test
    void verifyDecodesNameScopesConstraintsLimitsAndUsage() {
        mock.reply(200, "{\"object\":\"api_key_verification\",\"valid\":true,\"id\":\"ak_1\",\"name\":\"CI\","
                + "\"scopes\":[\"read\"],\"constraints\":{\"ip\":\"10.0.0.0/8\"},\"limits\":" + LIMITS
                + ",\"usage\":[" + STATE + "]}");

        ApiKeyVerification v = atlas.apiKeys().verify("ak_secret");

        assertTrue(v.valid());
        assertEquals("CI", v.name());
        assertEquals(List.of("read"), v.scopes());
        assertEquals("10.0.0.0/8", v.constraints().get("ip"));
        assertEquals(2, v.limits().size());
        assertEquals(990, v.usage().get(0).remaining());
    }

    @Test
    void sessionTouch() {
        mock.reply(200, "{\"object\":\"session\",\"id\":\"sess_1\",\"user_id\":\"user_1\",\"status\":\"active\","
                + "\"last_active_at\":5,\"expire_at\":9,\"abandon_at\":7,\"idle_expires_at\":7,\"created_at\":1,"
                + "\"extended\":true}");

        SessionTouch s = atlas.sessions().touch("sess_1");

        assertEquals("POST", mock.method);
        assertEquals("/v1/sessions/sess_1/touch", mock.path);
        assertTrue(mock.requestBody == null || mock.requestBody.isEmpty());
        assertTrue(s.extended());
        assertEquals(7, s.idleExpiresAt());
        assertEquals("user_1", s.userId());
    }

    @Test
    void sessionTouchOnAnEndedSessionThrows() {
        mock.reply(409, "{\"errors\":[{\"code\":\"SESSION_IDLE_EXPIRED\",\"message\":\"ended\"}]}");
        ConflictException ex = assertThrows(ConflictException.class, () -> atlas.sessions().touch("sess_1"));
        assertTrue(ex.hasCode(ErrorCodes.SESSION_IDLE_EXPIRED));
    }

    @Test
    void sessionReadsCarryIdleExpiresAt() {
        mock.reply(200, "{\"object\":\"session\",\"id\":\"sess_1\",\"last_active_at\":5,\"expire_at\":9,"
                + "\"abandon_at\":7,\"idle_expires_at\":7,\"created_at\":1}");
        Session s = atlas.sessions().get("sess_1");
        assertEquals(7L, s.idleExpiresAt());
    }

    @Test
    void userSessionListCarriesIdleExpiresAtOrgAndImpersonator() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[{\"object\":\"session\",\"id\":\"sess_1\","
                + "\"user_id\":\"user_1\",\"status\":\"active\",\"last_active_organization_id\":\"org_1\","
                + "\"impersonated_by\":\"user_admin\",\"last_active_at\":5,\"expire_at\":9,\"abandon_at\":7,"
                + "\"idle_expires_at\":7,\"created_at\":1},{\"object\":\"session\",\"id\":\"sess_2\","
                + "\"last_active_organization_id\":null,\"impersonated_by\":null,\"created_at\":1}],\"has_more\":false}");

        ListPage<UserSession> page = atlas.users().listSessions("user_1");

        assertEquals("GET", mock.method);
        assertEquals("/v1/users/user_1/sessions", mock.path);
        UserSession s = page.data().get(0);
        assertEquals(7L, s.idleExpiresAt());
        assertEquals(7L, s.abandonAt());
        assertEquals("org_1", s.lastActiveOrganizationId());
        assertEquals("user_admin", s.impersonatedBy());
        UserSession bare = page.data().get(1);
        assertNull(bare.idleExpiresAt());
        assertNull(bare.lastActiveOrganizationId());
        assertNull(bare.impersonatedBy());
    }

    @Test
    void userCreateAndImportSendNotify() throws IOException {
        mock.reply(201, "{\"object\":\"user\",\"id\":\"user_1\"}");
        atlas.users().create(new CreateUserBody("ada@example.com").notify(false));
        assertFalse(sent().get("notify").asBoolean(true));

        mock.reply(202, "{\"object\":\"job\",\"id\":\"job_1\"}");
        atlas.importExport().importUsers(new ImportExportImportUsersBody(List.of()).notify(false));
        assertEquals("/v1/user_imports", mock.path);
        assertFalse(sent().get("notify").asBoolean(true));
    }

    @Test
    void legacyRecordConstructorsStillCompile() {
        assertNull(new ApiKey("api_key", "ak_1", "user", "u", null, "ak_x", null, null, null, null, 1L).limits());
        assertNull(new ApiKeyVerification("v", false, null, null, null, null, null).usage());
        assertNull(new ApiKeyWithSecret("api_key", "ak_1", "user", "u", null, "ak_x", null, null, null, null, 1L,
                "ak_s", null).usage());
        assertNull(new Session("session", "s", "u", "active", null, null, 1, 2, 3, 4).idleExpiresAt());
        UserSession legacy = new UserSession("session", "s", "u", "active", 1, 2, 3, 4);
        assertNull(legacy.idleExpiresAt());
        assertNull(legacy.lastActiveOrganizationId());
        assertNull(legacy.impersonatedBy());
    }

    @Test
    void apiKeyWebhookDataDecodesFromAPayload() throws IOException {
        ObjectMapper snake = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        WebhookPayload payload = snake.readValue("{\"id\":\"evt_1\",\"type\":\"api_key.limit_reached\",\"timestamp\":1,"
                + "\"data\":{\"object\":\"api_key\",\"id\":\"ak_1\",\"prefix\":\"ak_x\",\"subject_type\":\"user\","
                + "\"subject_id\":\"u\",\"name\":\"CI\",\"claims\":{},\"scopes\":null,\"constraints\":{},\"limits\":" + LIMITS
                + ",\"max_token_lifetime_seconds\":3600,\"expires_at\":null,\"revoked_at\":null,\"created_at\":1,"
                + "\"updated_at\":2,\"limit\":" + STATE + "}}", WebhookPayload.class);

        ApiKeyWebhookData data = snake.treeToValue(payload.data(), ApiKeyWebhookData.class);

        assertEquals("CI", data.name());
        assertEquals(3600L, data.maxTokenLifetimeSeconds());
        assertEquals(2L, data.updatedAt());
        assertEquals(2, data.limits().size());
        assertEquals("requests", data.limit().unit());
        assertNull(data.scopes());
    }
}
