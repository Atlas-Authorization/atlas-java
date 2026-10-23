package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.CreatePolicyBody;
import net.atlasauth.atlas.models.CreatePolicyVersionBody;
import net.atlasauth.atlas.models.DeletedPolicy;
import net.atlasauth.atlas.models.Policy;
import net.atlasauth.atlas.models.PolicyAcceptancePage;
import net.atlasauth.atlas.models.PolicySettings;
import net.atlasauth.atlas.models.PolicySource;
import net.atlasauth.atlas.models.PolicySourceSync;
import net.atlasauth.atlas.models.PolicyVersion;
import net.atlasauth.atlas.models.UpdatePolicyBody;
import net.atlasauth.atlas.models.UpdatePolicySourceBody;
import net.atlasauth.atlas.models.UserPolicies;
import net.atlasauth.atlas.policy.PolicySourceProtocol;
import net.atlasauth.atlas.policy.PolicySourceProtocol.Failure;
import net.atlasauth.atlas.verify.SessionClaims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Policy acceptance: the Backend API resources and the policy source protocol helpers. */
class PolicyAcceptanceTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    // Vectors shared with the TypeScript reference (packages/backend policy-source).
    private static final String SECRET = "whsec_test_secret_0123456789";
    private static final long TS = 1760000000000L;
    private static final String SIG_KEY = "YfnQOCSMi2qm50WuVdA/aIV4avYpC6Hms9wdKrZcgHI=";
    private static final String SIG_FULL = "KT51ui3s0XNh9yr/2FedfQgVWCpYAMLQRKOvohc9VTM=";
    private static final String SIG_EVENT = "1q9qbfr02vQN6Xwsj0NjzR65jJiWo39qg8+JutS9ebs=";

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

    private static final String POLICY = "{\"object\":\"policy\",\"id\":\"pol_1\",\"key\":\"tos\",\"title\":\"Terms\",\"display\":\"scroll\","
            + "\"required\":true,\"applies_to\":\"sign_in_and_sign_up\",\"reaccept\":\"grace\",\"grace_days\":14,\"source\":\"atlas\","
            + "\"position\":0,\"status\":\"active\",\"overrides\":[],\"current_version\":{\"id\":\"polv_1\",\"version\":\"2026-10-24\","
            + "\"status\":\"published\",\"content_sha256\":\"abc\",\"published_at\":1,\"effective_at\":2},\"upcoming_version\":null,"
            + "\"latest_version\":null,\"retired_at\":null,\"created_at\":1,\"updated_at\":2}";

    @Test
    void createAndReadAPolicy() throws Exception {
        mock.reply(201, POLICY);
        Policy p = atlas.policies().create(new CreatePolicyBody().key("tos").title("Terms").display("scroll")
                .required(true).appliesTo("sign_in_and_sign_up").reaccept("grace").graceDays(14));
        assertEquals("POST", mock.method);
        assertEquals("/v1/policies", mock.path);
        JsonNode body = JSON.readTree(mock.requestBody);
        assertEquals("tos", body.get("key").asText());
        assertEquals("sign_in_and_sign_up", body.get("applies_to").asText());
        assertEquals(14, body.get("grace_days").asInt());
        assertEquals("polv_1", p.currentVersion().id());
        assertEquals(14, p.graceDays());
        assertNull(p.upcomingVersion());
    }

    @Test
    void updateClearsGraceDaysWithAnExplicitNull() throws Exception {
        mock.reply(200, POLICY);
        atlas.policies().update("tos", new UpdatePolicyBody().reaccept("none").clear("grace_days"));
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/policies/tos", mock.path);
        JsonNode body = JSON.readTree(mock.requestBody);
        assertTrue(body.has("grace_days") && body.get("grace_days").isNull());
        assertEquals("none", body.get("reaccept").asText());
    }

    @Test
    void deleteReportsRetireVersusDelete() {
        mock.reply(200, "{\"object\":\"policy\",\"id\":\"pol_1\",\"key\":\"tos\",\"deleted\":false,\"retired\":true}");
        DeletedPolicy d = atlas.policies().delete("pol_1");
        assertEquals("DELETE", mock.method);
        assertTrue(d.retired());
        assertFalse(d.deleted());
    }

    @Test
    void versionsPublishRetireAndDrafts() throws Exception {
        mock.reply(201, "{\"object\":\"policy_version\",\"id\":\"polv_2\",\"policy_id\":\"pol_1\",\"policy\":\"tos\",\"version\":\"2026-11-01\","
                + "\"status\":\"draft\",\"content_html\":\"<p>x</p>\",\"content_sha256\":\"h\",\"created_at\":1,\"updated_at\":1}");
        PolicyVersion v = atlas.policies().createVersion("tos", new CreatePolicyVersionBody().version("2026-11-01")
                .contentMarkdown("x").summaryOfChanges("Fees"));
        assertEquals("/v1/policies/tos/versions", mock.path);
        assertEquals("draft", v.status());
        assertEquals("Fees", JSON.readTree(mock.requestBody).get("summary_of_changes").asText());

        mock.reply(200, "{\"object\":\"policy_version\",\"id\":\"polv_2\",\"status\":\"published\"}");
        atlas.policies().publishVersion("tos", "2026-11-01", 1760000000000L);
        assertEquals("POST", mock.method);
        assertEquals("/v1/policies/tos/versions/2026-11-01/publish", mock.path);
        assertEquals(1760000000000L, JSON.readTree(mock.requestBody).get("effective_at").asLong());

        atlas.policies().publishVersion("tos", "2026-11-01", "2026-12-01T00:00:00Z");
        assertEquals("2026-12-01T00:00:00Z", JSON.readTree(mock.requestBody).get("effective_at").asText());

        atlas.policies().retireVersion("tos", "polv_2");
        assertEquals("/v1/policies/tos/versions/polv_2/retire", mock.path);

        mock.reply(200, "{\"object\":\"policy_version\",\"id\":\"polv_2\",\"deleted\":true}");
        assertTrue(atlas.policies().deleteVersion("tos", "polv_2").deleted());
        assertEquals("DELETE", mock.method);
    }

    @Test
    void acceptancesPageAndCsv() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[{\"object\":\"policy_acceptance\",\"id\":\"c1\",\"user_id\":\"user_1\",\"policy\":\"tos\","
                + "\"version\":\"2026-10-24\",\"status\":\"accepted\",\"scrolled_to_end\":true,\"view_ms\":4200,\"accepted_at\":5}],"
                + "\"has_more\":true,\"next_cursor\":\"c1\"}");
        PolicyAcceptancePage page = atlas.policies().listAcceptances("tos", "2026-10-24", "all", 10, null);
        assertEquals("/v1/policies/tos/acceptances", mock.path);
        assertTrue(mock.rawQuery.contains("version=2026-10-24") && mock.rawQuery.contains("status=all") && mock.rawQuery.contains("limit=10"));
        assertTrue(page.hasMore());
        assertEquals("c1", page.nextCursor());
        assertEquals(4200, page.data().get(0).viewMs());

        mock.reply(200, "user_id,policy\nuser_1,tos\n");
        String csv = atlas.policies().exportAcceptancesCsv("tos", null, null);
        assertTrue(mock.rawQuery.contains("format=csv"));
        assertTrue(csv.startsWith("user_id,policy"));
    }

    @Test
    void userStandingAndSettings() throws Exception {
        mock.reply(200, "{\"object\":\"list\",\"user_id\":\"user_1\",\"needs_acceptance\":true,\"data\":[{\"object\":\"policy_standing\",\"policy\":\"tos\","
                + "\"needs_reaccept\":true,\"blocking\":false,\"grace_ends_at\":9}]}");
        UserPolicies u = atlas.policies().forUser("user_1");
        assertEquals("/v1/users/user_1/policies", mock.path);
        assertTrue(u.needsAcceptance());
        assertEquals(9L, u.data().get(0).graceEndsAt());

        mock.reply(200, "{\"object\":\"policy_settings\",\"session_claim\":true}");
        PolicySettings s = atlas.policies().updateSettings(true);
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/instance/policy_settings", mock.path);
        assertTrue(JSON.readTree(mock.requestBody).get("session_claim").asBoolean());
        assertTrue(s.sessionClaim());
    }

    @Test
    void policySourceSecretIsWriteOnly() throws Exception {
        mock.reply(200, "{\"object\":\"policy_source\",\"enabled\":true,\"endpoint_url\":\"https://cms.example.com/atlas\",\"signing_secret_set\":true,"
                + "\"sync\":\"webhook\",\"poll_interval_minutes\":60,\"webhook_url\":\"https://api.example.com/v1/webhooks/policies/ins_1\","
                + "\"protocol_version\":\"2026-10-24\"}");
        PolicySource src = atlas.policySource().update(new UpdatePolicySourceBody().enabled(true)
                .endpointUrl("https://cms.example.com/atlas").signingSecret(SECRET).sync("webhook"));
        assertEquals("PUT", mock.method);
        assertEquals("/v1/instance/policy_source", mock.path);
        assertEquals(SECRET, JSON.readTree(mock.requestBody).get("signing_secret").asText());
        assertTrue(src.signingSecretSet());
        assertFalse(mock.responseBody.contains(SECRET));

        mock.reply(200, "{\"object\":\"policy_source_sync\",\"ok\":true,\"warnings\":[],\"changes\":{\"created\":[\"tos\"],\"updated\":[],"
                + "\"published\":[\"tos\"],\"unchanged\":[],\"rejected\":[],\"retired\":[]}}");
        PolicySourceSync sync = atlas.policySource().sync();
        assertEquals("/v1/instance/policy_source/sync", mock.path);
        assertEquals("tos", sync.changes().created().get(0));

        atlas.policySource().test();
        assertEquals("/v1/instance/policy_source/test", mock.path);
        atlas.policySource().listDeliveries(5, null, "fetch", "failed");
        assertEquals("/v1/instance/policy_source/deliveries", mock.path);
        assertTrue(mock.rawQuery.contains("kind=fetch"));
        atlas.policySource().delete();
        assertEquals("DELETE", mock.method);
    }

    // ---- policy source protocol ----

    private static java.util.function.Function<String, String> headers(String id, long ts, String sig) {
        Map<String, String> m = new java.util.HashMap<>();
        if (id != null) m.put("atlas-id", id);
        m.put("atlas-timestamp", String.valueOf(ts));
        if (sig != null) m.put("atlas-signature", sig);
        m.put("atlas-policies-version", "2026-10-24");
        return m::get;
    }

    @Test
    void verifiesAtlasFeedRequestAgainstTheReferenceVectors() {
        var r = PolicySourceProtocol.verifyFeedRequest("GET", "/atlas/policies?key=tos",
                headers("msg_01", TS, "v1," + SIG_KEY), SECRET, TS + 1000, PolicySourceProtocol.TOLERANCE_MS);
        assertTrue(r.verified());
        assertEquals("tos", r.key());
        assertEquals("msg_01", r.deliveryId());
        assertEquals("2026-10-24", r.protocolVersion());

        var full = PolicySourceProtocol.verifyFeedRequest("get", "/atlas/policies",
                headers("msg_01", TS, "v1," + SIG_FULL), SECRET, TS, PolicySourceProtocol.TOLERANCE_MS);
        assertTrue(full.verified());
        assertNull(full.key());
    }

    @Test
    void acceptsAnyOfSeveralV1ValuesAndIgnoresOtherVersions() {
        String sig = "v1,AAAA v2,zzzz v1," + SIG_KEY;
        assertTrue(PolicySourceProtocol.verifyFeedRequest("GET", "/atlas/policies?key=tos", headers("msg_01", TS, sig), SECRET, TS, 300000).verified());
    }

    @Test
    void refusesBadFeedRequests() {
        long now = TS;
        assertEquals(Failure.SIGNATURE_MISMATCH, PolicySourceProtocol.verifyFeedRequest("GET", "/atlas/policies?key=other",
                headers("msg_01", TS, "v1," + SIG_KEY), SECRET, now, 300000).failure());
        assertEquals(Failure.SIGNATURE_MISMATCH, PolicySourceProtocol.verifyFeedRequest("GET", "/atlas/policies?key=tos",
                headers("msg_01", TS, "v1," + SIG_KEY), "wrong-secret", now, 300000).failure());
        assertEquals(Failure.STALE_TIMESTAMP, PolicySourceProtocol.verifyFeedRequest("GET", "/atlas/policies?key=tos",
                headers("msg_01", TS, "v1," + SIG_KEY), SECRET, now + 300001, 300000).failure());
        assertTrue(PolicySourceProtocol.verifyFeedRequest("GET", "/atlas/policies?key=tos",
                headers("msg_01", TS, "v1," + SIG_KEY), SECRET, now + 300000, 300000).verified());
        assertEquals(Failure.MISSING_HEADERS, PolicySourceProtocol.verifyFeedRequest("GET", "/x", headers(null, TS, "v1,AA"), SECRET, now, 300000).failure());
        assertEquals(Failure.MALFORMED_SIGNATURE, PolicySourceProtocol.verifyFeedRequest("GET", "/x", headers("a", TS, "v2,AA"), SECRET, now, 300000).failure());
        assertEquals(Failure.WRONG_METHOD, PolicySourceProtocol.verifyFeedRequest("POST", "/x", headers("a", TS, "v1,AA"), SECRET, now, 300000).failure());
    }

    @Test
    void signsEventsLikeTheReference() throws Exception {
        String body = "{\"occurred_at\":\"2026-10-24T00:00:00.000Z\",\"type\":\"policy.published\",\"key\":\"tos\",\"version\":\"2026-10-24\"}";
        var signed = PolicySourceProtocol.signBody(body, SECRET, "evt_1", TS);
        assertEquals(body, signed.body());
        assertEquals("v1," + SIG_EVENT, signed.headers().get("atlas-signature"));
        assertEquals("evt_1", signed.headers().get("atlas-id"));
        assertEquals(String.valueOf(TS), signed.headers().get("atlas-timestamp"));

        var pub = PolicySourceProtocol.signPublished("tos", "2026-10-24", SECRET);
        JsonNode j = JSON.readTree(pub.body());
        assertEquals("policy.published", j.get("type").asText());
        assertEquals("2026-10-24", j.get("version").asText());
        assertTrue(j.has("occurred_at"));
        assertTrue(pub.headers().get("atlas-id").startsWith("evt_"));
        var ret = PolicySourceProtocol.signRetired("tos", SECRET);
        assertFalse(JSON.readTree(ret.body()).has("version"));
        assertThrows(IllegalArgumentException.class, () -> PolicySourceProtocol.signEvent("policy.published", "tos", null, SECRET, null, null));
        assertThrows(IllegalArgumentException.class, () -> PolicySourceProtocol.signEvent("nope", "tos", null, SECRET, null, null));
    }

    @Test
    void polClaimAndConstants() {
        SessionClaims c = new SessionClaims(Map.of("pol", Map.of("tos", "2026-10-24")));
        assertEquals("2026-10-24", c.pol().get("tos"));
        assertTrue(c.hasAcceptedPolicy("tos", "2026-10-24"));
        assertFalse(c.hasAcceptedPolicy("tos", "old"));
        assertTrue(new SessionClaims(Map.of()).pol().isEmpty());
        assertEquals("user.policy_accepted", AtlasConstants.WEBHOOK_EVENT_USER_POLICY_ACCEPTED);
        assertEquals("policy.version_published", AtlasConstants.WEBHOOK_EVENT_POLICY_VERSION_PUBLISHED);
    }
}
