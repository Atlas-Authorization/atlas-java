package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.CreatePartnerLinkBody;
import net.atlasauth.atlas.models.CreatePlatformAppBody;
import net.atlasauth.atlas.models.CreatePrivacyRequestBody;
import net.atlasauth.atlas.models.CreateSecretKeyBody;
import net.atlasauth.atlas.models.CreateWebhookEndpointBody;
import net.atlasauth.atlas.models.CreateOAuthClientBody;
import net.atlasauth.atlas.models.ListEventsParams;
import net.atlasauth.atlas.models.ListConfigChangelogParams;
import net.atlasauth.atlas.models.ListPlatformAppsParams;
import net.atlasauth.atlas.models.PrivacyLookupBody;
import net.atlasauth.atlas.models.RotateSecretKeyBody;
import net.atlasauth.atlas.models.SaveOutboundPartnerLinkBody;
import net.atlasauth.atlas.models.SetIntegrationBody;
import net.atlasauth.atlas.models.SetPrivacyOrchestratorBody;
import net.atlasauth.atlas.models.UpdateSecretKeyBody;
import net.atlasauth.atlas.models.UserSearchParams;
import net.atlasauth.atlas.models.UpsertWebhookEndpointBody;
import net.atlasauth.atlas.models.WebhookWindowBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The wire surface added by the integrator-spec program: method, path, query and body of each route. */
class SpSurfaceTest {

    @FunctionalInterface
    private interface Call {
        void run() throws Exception;
    }

    private record Case(String method, String path, String query, String body, Call call) {
    }

    private static Case c(String method, String path, String query, String body, Call call) {
        return new Case(method, path, query, body, call);
    }

    private MockBackend mock;
    private AtlasClient atlas;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        mock.reply(200, "{\"object\":\"x\",\"data\":[],\"changes\":[],\"results\":[]}");
        atlas = AtlasClient.builder().secretKey("sk_test_123").platformKey("psk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private static List<String> queryPairs(String raw) {
        List<String> out = new ArrayList<>();
        if (raw != null && !raw.isEmpty()) {
            for (String part : raw.split("&")) {
                out.add(URLDecoder.decode(part, StandardCharsets.UTF_8));
            }
        }
        Collections.sort(out);
        return out;
    }

    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    @Test
    void routesSendTheDocumentedMethodPathQueryAndBody() throws Exception {
        List<Case> cases = List.of(
                // credentials
                c("GET", "/v1/whoami", "", "", () -> atlas.whoami()),
                c("POST", "/v1/secret_keys", "", "{\"name\":\"ci\",\"scopes\":[\"read_only\"],\"key_format\":\"legacy\"}",
                        () -> atlas.secretKeys().create(new CreateSecretKeyBody().name("ci").scopes(List.of("read_only")).keyFormat("legacy"))),
                c("POST", "/v1/secret_keys", "", "{\"scopes\":[\"a\"],\"expires_at\":\"2027-01-01T00:00:00Z\"}",
                        () -> atlas.secretKeys().create(new CreateSecretKeyBody().scopes(List.of("a")).expiresAtIso("2027-01-01T00:00:00Z"))),
                c("GET", "/v1/secret_keys", "", "", () -> atlas.secretKeys().list()),
                c("GET", "/v1/secret_keys/key_1", "", "", () -> atlas.secretKeys().get("key_1")),
                c("PATCH", "/v1/secret_keys/key_1", "", "{\"name\":\"x\",\"owner\":null}",
                        () -> atlas.secretKeys().update("key_1", new UpdateSecretKeyBody().name("x").clear("owner"))),
                c("POST", "/v1/secret_keys/key_1/rotate", "", "{\"overlap_seconds\":0}",
                        () -> atlas.secretKeys().rotate("key_1", new RotateSecretKeyBody().overlapSeconds(0))),
                c("POST", "/v1/secret_keys/key_1/revoke", "", "", () -> atlas.secretKeys().revoke("key_1")),
                c("GET", "/v1/keys", "", "", () -> atlas.keys().list()),
                c("POST", "/v1/partner_links", "", "{\"partner\":\"cookiemunch\",\"scopes\":[\"privacy:execute\"]}",
                        () -> atlas.partnerLinks().create(new CreatePartnerLinkBody().partner("cookiemunch").scopes(List.of("privacy:execute")))),
                c("GET", "/v1/partner_links", "", "", () -> atlas.partnerLinks().list()),
                c("GET", "/v1/partner_links/pl_1", "", "", () -> atlas.partnerLinks().get("pl_1")),
                c("POST", "/v1/partner_links/pl_1/rotate_secret", "", "", () -> atlas.partnerLinks().rotateSecret("pl_1")),
                c("DELETE", "/v1/partner_links/pl_1", "", "", () -> atlas.partnerLinks().revoke("pl_1")),
                c("GET", "/v1/partner_links/outbound", "", "", () -> atlas.partnerLinks().outbound().list()),
                c("PUT", "/v1/partner_links/outbound/cookiemunch", "", "{\"client_id\":\"cid\",\"client_secret\":\"sec\"}",
                        () -> atlas.partnerLinks().outbound().save("cookiemunch", new SaveOutboundPartnerLinkBody().clientId("cid").clientSecret("sec"))),
                c("POST", "/v1/partner_links/outbound/cookiemunch/test", "", "", () -> atlas.partnerLinks().outbound().test("cookiemunch")),
                c("DELETE", "/v1/partner_links/outbound/cookiemunch", "", "", () -> atlas.partnerLinks().outbound().remove("cookiemunch")),
                c("GET", "/v1/atlas_id_apps", "", "", () -> atlas.atlasIdGrants().listApps()),
                c("GET", "/v1/atlas_id_apps/app_1/users", "email=a@b.com", "", () -> atlas.atlasIdGrants().lookup("app_1", "a@b.com")),
                c("DELETE", "/v1/atlas_id_apps/app_1/grants/g_1", "", "", () -> atlas.atlasIdGrants().revoke("app_1", "g_1")),
                // platform credentials, audit and provisioning
                c("GET", "/v1/platform/whoami", "", "", () -> atlas.platform().whoami()),
                c("GET", "/v1/platform/keys", "kind=secret_key\nstatus=active", "", () -> atlas.platform().keys().list("secret_key", "active", null)),
                c("POST", "/v1/platform/keys", "", "{\"scopes\":[\"apps:read\"],\"key_format\":\"atlas\"}",
                        () -> atlas.platform().keys().create(map("scopes", List.of("apps:read"), "key_format", "atlas"))),
                c("PATCH", "/v1/platform/keys/k_1", "", "{\"name\":\"n\"}", () -> atlas.platform().keys().update("k_1", new UpdateSecretKeyBody().name("n"))),
                c("POST", "/v1/platform/keys/k_1/revoke", "", "", () -> atlas.platform().keys().revoke("k_1")),
                c("GET", "/v1/platform/audit_logs", "action=app.created\nlimit=5", "", () -> atlas.platform().audit().list("app.created", 5, null)),
                c("GET", "/v1/platform/audit_streams", "", "", () -> atlas.platform().audit().streams().list()),
                c("POST", "/v1/platform/audit_streams", "", "{\"url\":\"https://x.example.com/a\",\"format\":\"cloudevents\"}",
                        () -> atlas.platform().audit().streams().create(map("url", "https://x.example.com/a", "format", "cloudevents"))),
                c("PATCH", "/v1/platform/audit_streams/s_1", "", "{\"active\":true}", () -> atlas.platform().audit().streams().update("s_1", map("active", true))),
                c("DELETE", "/v1/platform/audit_streams/s_1", "", "", () -> atlas.platform().audit().streams().delete("s_1")),
                c("POST", "/v1/platform/audit_streams/s_1/test", "", "", () -> atlas.platform().audit().streams().test("s_1")),
                c("POST", "/v1/platform/apps", "", "{\"name\":\"A\",\"external_ref\":\"ref-1\",\"metadata\":{\"product\":\"x\",\"sandbox\":true}}",
                        () -> atlas.platform().apps().create(new CreatePlatformAppBody().name("A").externalRef("ref-1").metadata(map("product", "x", "sandbox", true)))),
                c("GET", "/v1/platform/apps", "metadata[product]=x", "", () -> atlas.platform().apps().list(new ListPlatformAppsParams().metadata(map("product", "x")))),
                c("POST", "/v1/platform/apps/app_1/secret_keys", "", "{\"name\":\"ci\",\"external_ref\":\"k-1\"}",
                        () -> atlas.platform().apps().createSecretKey("app_1", map("name", "ci", "external_ref", "k-1"))),
                c("GET", "/v1/platform/apps/app_1/secret_keys", "metadata[env]=ci", "", () -> atlas.platform().apps().listSecretKeys("app_1", map("env", "ci"))),
                c("POST", "/v1/platform/apps/app_1/clone_config", "", "{\"dry_run\":true,\"to_environment\":\"development\"}",
                        () -> atlas.platform().apps().cloneConfig("app_1", map("to_environment", "development", "dry_run", true))),
                c("POST", "/v1/platform/apps/bulk_delete", "", "{\"dry_run\":true,\"metadata\":{\"sandbox\":true}}",
                        () -> atlas.platform().apps().sandboxTeardown(null, true)),
                c("GET", "/v1/platform/atlas_id_apps", "metadata[k]=v", "", () -> atlas.platform().atlasIdApps().list(map("k", "v"))),
                // webhooks
                c("GET", "/v1/webhook_endpoints/we_1", "", "", () -> atlas.webhooks().endpoints().get("we_1")),
                c("GET", "/v1/webhook_endpoints", "include_deprecated=true\nmetadata[a]=b", "",
                        () -> atlas.webhooks().endpoints().listWithEvents(true, map("a", "b"))),
                c("PUT", "/v1/webhook_endpoints/by_url", "", "{\"url\":\"https://app.example.com/hook\",\"signature_versions\":[\"v1\",\"v2\"],\"event_naming\":\"canonical\"}",
                        () -> atlas.webhooks().endpoints().upsertByUrl(new UpsertWebhookEndpointBody("https://app.example.com/hook")
                                .signatureVersions(List.of("v1", "v2")).eventNaming("canonical"))),
                c("POST", "/v1/webhook_endpoints", "", "{\"enabled_events\":[\"*\"],\"destination\":{\"type\":\"aws_eventbridge\",\"region\":\"us-east-1\",\"event_bus_name\":\"default\"},\"destination_credentials\":{\"access_key_id\":\"a\",\"secret_access_key\":\"b\"}}",
                        () -> atlas.webhooks().endpoints().create(new CreateWebhookEndpointBody().enabledEvents(List.of("*"))
                                .destination(map("type", "aws_eventbridge", "region", "us-east-1", "event_bus_name", "default"))
                                .destinationCredentials(map("access_key_id", "a", "secret_access_key", "b")))),
                c("POST", "/v1/webhook_endpoints/we_1/test", "wait=5s", "", () -> atlas.webhooks().endpoints().testNow("we_1", "5s")),
                c("POST", "/v1/webhook_endpoints/we_1/redeliver", "", "{\"since\":5,\"status\":\"failed\"}",
                        () -> atlas.webhooks().endpoints().redeliverWindow("we_1", new WebhookWindowBody().since(5L).status("failed"))),
                c("POST", "/v1/webhook_endpoints/we_1/replay", "", "{\"since\":9,\"event_type\":\"user.created\"}",
                        () -> atlas.webhooks().endpoints().replay("we_1", new WebhookWindowBody().since(9L).eventType("user.created"))),
                c("GET", "/v1/webhook_endpoints/we_1/deliveries", "event_type=user.created\nlimit=500\nsince=1\nstarting_after=c\nstatus=failed\nuntil=2", "",
                        () -> atlas.webhooks().deliveries("we_1", 500, "c", "failed", "user.created", 1L, 2L)),
                c("GET", "/v1/meta/egress_ips", "", "", () -> atlas.delivery().egressIps()),
                c("GET", "/v1/delivery_health", "kind=webhook_endpoint\nstate=unhealthy", "",
                        () -> atlas.delivery().health("webhook_endpoint", "unhealthy")),
                c("PUT", "/v1/delivery_health/webhook_endpoint/we_1/alert_contacts", "", "{\"alert_contacts\":[\"a@b.com\"]}",
                        () -> atlas.delivery().setAlertContacts("webhook_endpoint", "we_1", List.of("a@b.com"))),
                // events, users, audit
                c("GET", "/v1/events", "order=desc\nsince=2026-01-01T00:00:00Z", "",
                        () -> atlas.analytics().events(new ListEventsParams().since("2026-01-01T00:00:00Z").order("desc"))),
                c("GET", "/v1/users", "order_by=-updated_at\nupdated_after=100\nupdated_before=200", "",
                        () -> atlas.users().list(new UserSearchParams().updatedAfter(100L).updatedBefore(200L).orderBy("-updated_at"))),
                c("GET", "/v1/organizations/org_1/memberships", "limit=2\nstarting_after=c", "",
                        () -> atlas.organizations().memberships().list("org_1", 2, "c")),
                c("GET", "/v1/app_roles/admin/users", "limit=2\nstarting_after=c", "", () -> atlas.appRoles().listUsersAfter("admin", 2, "c")),
                c("GET", "/v1/logout_notify_endpoints/lne_1/deliveries", "starting_after=c", "",
                        () -> atlas.logoutNotifyEndpoints().listDeliveries("lne_1", null, "c")),
                c("GET", "/v1/api_keys/revoked", "starting_after=c", "", () -> atlas.apiKeys().listRevoked(null, null, "c")),
                c("GET", "/v1/messaging_providers/email", "", "", () -> atlas.messaging().channel("email")),
                c("GET", "/v1/config_changelog", "include_no_op=false", "",
                        () -> atlas.configChangelog().list(new ListConfigChangelogParams().includeNoOp(false))),
                c("POST", "/v1/oauth_clients", "", "{\"name\":\"A\",\"redirect_uris\":[\"https://a/cb\"],\"require_pkce\":false,\"external_ref\":\"r\"}",
                        () -> atlas.oauthClients().create(new CreateOAuthClientBody("A", List.of("https://a/cb")).requirePkce(false).externalRef("r"))),
                // privacy executor
                c("GET", "/v1/privacy", "", "", () -> atlas.privacy().discover()),
                c("GET", "/v1/privacy/presets/cookiemunch", "", "", () -> atlas.privacy().preset("cookiemunch")),
                c("POST", "/v1/privacy/lookup", "", "{\"atlas\":{\"iss\":\"https://i\",\"sub\":\"u1\"},\"emails\":[\"a@b.com\"]}",
                        () -> atlas.privacy().lookup(new PrivacyLookupBody().emails(List.of("a@b.com")).atlas(map("iss", "https://i", "sub", "u1")))),
                c("POST", "/v1/privacy/requests", "", "{\"subjectId\":\"u1\",\"type\":\"deletion\",\"caseId\":\"c1\",\"executeAfter\":1700000000000}",
                        () -> atlas.privacy().createRequest(new CreatePrivacyRequestBody().subjectId("u1").type("deletion").caseId("c1").executeAfterMs(1700000000000L), "k")),
                c("GET", "/v1/privacy/requests/pr_1", "", "", () -> atlas.privacy().getRequest("pr_1")),
                c("POST", "/v1/privacy/requests/pr_1/cancel", "", "", () -> atlas.privacy().cancelRequest("pr_1")),
                c("GET", "/v1/instance/privacy_orchestrator", "", "", () -> atlas.privacy().orchestrator().get()),
                c("PUT", "/v1/instance/privacy_orchestrator", "", "{\"provider\":\"cookiemunch\",\"sign_requests\":true,\"cancel_url\":null}",
                        () -> atlas.privacy().orchestrator().set(new SetPrivacyOrchestratorBody().provider("cookiemunch").signRequests(true).clear("cancel_url"))),
                c("DELETE", "/v1/instance/privacy_orchestrator", "", "", () -> atlas.privacy().orchestrator().delete()),
                // integrations
                c("GET", "/v1/instance/integrations", "", "", () -> atlas.instance().listIntegrations()),
                c("GET", "/v1/instance/integrations/chatly", "", "", () -> atlas.instance().getIntegration("chatly")),
                c("PUT", "/v1/instance/integrations/acme", "", "{\"enabled\":true,\"name\":\"Acme\",\"ttl_seconds\":3600,\"events_url\":null}",
                        () -> atlas.instance().setIntegration("acme", new SetIntegrationBody().enabled(true).name("Acme").ttlSeconds(3600).clear("events_url"))),
                c("DELETE", "/v1/instance/integrations/acme", "", "", () -> atlas.instance().deleteIntegration("acme")),
                c("GET", "/v1/instance/integrations/acme/deliveries", "kind=event\nlimit=5\ntrace=t1", "",
                        () -> atlas.instance().integrationDeliveries("acme", 5, "t1", "event")),
                c("POST", "/v1/instance/integrations/acme/events_secret", "", "", () -> atlas.instance().revealIntegrationEventsSecret("acme")),
                c("POST", "/v1/instance/integrations/acme/atlas_id_tokens", "", "{\"access_token\":\"at\"}",
                        () -> atlas.instance().integrationAtlasIdToken("acme", "at")),
                c("POST", "/v1/sessions/sess_1/integration_tokens/chatly", "", "", () -> atlas.sessions().getIntegrationToken("sess_1", "chatly")),
                c("POST", "/v1/sessions/sess_1/tokens/supabase", "", "", () -> atlas.sessions().createToken("sess_1", "supabase")),
                c("POST", "/v1/integrations/verify_errors", "", "{\"integration\":\"chatly\",\"error\":\"expired\"}",
                        () -> atlas.integrations().reportVerifyError("chatly", "expired")),
                c("GET", "/v1/integrations/chatly/card", "iss=https://i\nsub=u1", "", () -> atlas.integrations().card("chatly", "u1", "https://i")),
                c("POST", "/v1/integrations/chatly/card/actions/revoke_sessions", "", "{\"agent\":{\"name\":\"Ann\"},\"sub\":\"u1\"}",
                        () -> atlas.integrations().revokeSessions("chatly", "u1", null, map("name", "Ann"))),
                c("GET", "/v1/public/instances/ins_1", "", "", () -> atlas.integrations().instanceMetadata("ins_1")),
                // entitlements, notices, advisories, sub-processors, audit export
                c("GET", "/v1/entitlements", "subject_id=org_1\nsubject_type=org", "", () -> atlas.entitlements().get("org", "org_1")),
                c("GET", "/v1/instance/notices", "all=true", "", () -> atlas.notices().list(true)),
                c("POST", "/v1/instance/notices/n_1/acknowledge", "", "", () -> atlas.notices().acknowledge("n_1")),
                c("GET", "/v1/security_advisories", "", "", () -> atlas.securityAdvisories().list()),
                c("POST", "/v1/security_advisories/a_1/acknowledge", "", "", () -> atlas.securityAdvisories().acknowledge("a_1")),
                c("GET", "/v1/public/security_advisories", "", "", () -> atlas.securityAdvisories().catalog()),
                c("GET", "/v1/instance/sub_processors", "", "", () -> atlas.subProcessors().get()),
                c("GET", "/v1/audit_exports", "", "", () -> atlas.auditExports().list()),
                c("GET", "/v1/audit_exports/ae_1", "", "", () -> atlas.auditExports().get("ae_1")),
                c("POST", "/v1/audit_exports", "", "{\"name\":\"siem\",\"type\":\"webhook\",\"destination\":{\"url\":\"https://siem.example.com\"},\"backfill_hours\":24}",
                        () -> atlas.auditExports().create(map("name", "siem", "type", "webhook", "destination", map("url", "https://siem.example.com"), "backfill_hours", 24))),
                c("PATCH", "/v1/audit_exports/ae_1", "", "{\"action_filter\":null,\"enabled\":true}",
                        () -> atlas.auditExports().update("ae_1", map("enabled", true, "action_filter", null))),
                c("DELETE", "/v1/audit_exports/ae_1", "", "", () -> atlas.auditExports().delete("ae_1")),
                c("POST", "/v1/audit_exports/ae_1/test", "", "", () -> atlas.auditExports().test("ae_1")),
                c("POST", "/v1/audit_exports/ae_1/rotate_secret", "", "", () -> atlas.auditExports().rotateSecret("ae_1")),
                // status
                c("GET", "/v1/status", "", "", () -> atlas.status().service()),
                c("GET", "/v1/instance/status_subscription", "", "", () -> atlas.status().getSubscription()),
                c("PUT", "/v1/instance/status_subscription", "", "{\"component_ids\":[],\"min_impact\":\"major\",\"regions\":[\"eu\"]}",
                        () -> atlas.status().updateSubscription(map("regions", List.of("eu"), "component_ids", List.of(), "min_impact", "major"))),
                c("DELETE", "/v1/instance/status_subscription", "", "", () -> atlas.status().disableSubscription()),
                c("POST", "/v1/instance/status_subscription/rotate_feed", "", "", () -> atlas.status().rotateFeed()),
                c("GET", "/v1/instance/status_incidents", "limit=3\nopen=true", "", () -> atlas.status().listIncidents(true, 3)),
                c("GET", "/v1/instance/status_dependencies", "", "", () -> atlas.status().listDependencies()),
                c("POST", "/v1/instance/status_dependencies", "", "{\"name\":\"Stripe\",\"url\":\"https://status.stripe.com\"}",
                        () -> atlas.status().createDependency(map("name", "Stripe", "url", "https://status.stripe.com"))),
                c("GET", "/v1/instance/status_dependencies/d_1", "", "", () -> atlas.status().getDependency("d_1")),
                c("PATCH", "/v1/instance/status_dependencies/d_1", "", "{\"enabled\":true}", () -> atlas.status().updateDependency("d_1", map("enabled", true))),
                c("DELETE", "/v1/instance/status_dependencies/d_1", "", "", () -> atlas.status().deleteDependency("d_1")),
                c("POST", "/v1/instance/status_dependencies/d_1/refresh", "", "", () -> atlas.status().refreshDependency("d_1")),
                // instance extras
                c("GET", "/v1/instance/preflight", "include_suppressed=true\nmin_severity=warning", "", () -> atlas.instance().preflight("warning", true)),
                c("GET", "/v1/instance/unknown_fields_report", "days=7", "", () -> atlas.instance().unknownFieldsReport(7)),
                c("GET", "/v1/openapi/diff", "since=2026-10-01\nsurface=bapi", "", () -> atlas.instance().openapiDiff("2026-10-01", "bapi")),
                c("GET", "/v1/integration-health", "only=webhooks,email\nprobe=false", "", () -> atlas.instance().integrationHealth(List.of("webhooks", "email"), false)),
                c("GET", "/v1/integration-health/providers", "", "", () -> atlas.instance().integrationHealthProviders()),
                c("GET", "/v1/embed", "instance=ins_1\norigin=https://app.example.com\nshape=button", "",
                        () -> atlas.instance().embed("ins_1", "https://app.example.com", "button")),
                c("PUT", "/v1/instance/design", "", "{\"brand_url\":\"https://brand.example.com/tokens.json\"}",
                        () -> atlas.design().importBrandUrl("https://brand.example.com/tokens.json")),
                c("PUT", "/v1/instance/design", "", "{\"brand\":null}", () -> atlas.design().removeBrand()),
                // referrals
                c("POST", "/v1/instance/referrals/referalli/install", "", "{\"return_to\":\"https://app.example.com/done\"}",
                        () -> atlas.referrals().install("referalli", "https://app.example.com/done")));

        for (Case tc : cases) {
            mock.method = null;
            tc.call().run();
            String label = tc.method() + " " + tc.path();
            assertEquals(tc.method(), mock.method, label);
            assertEquals(tc.path(), mock.path, label);
            assertEquals(queryPairs(tc.query().replace("\n", "&")), queryPairs(mock.rawQuery), label + " query");
            if (tc.body().isEmpty()) {
                assertEquals("", mock.requestBody, label + " unexpected body");
            } else {
                assertEquals(om.readTree(tc.body()), om.readTree(mock.requestBody), label + " body");
            }
        }
        assertTrue(cases.size() > 100);
    }

    @Test
    void dryRunAddsTheQueryAndDropsTheIdempotencyKey() throws Exception {
        mock.reply(200, "{\"object\":\"dry_run\",\"dry_run\":true,\"mode\":\"plan\",\"validated\":false,\"method\":\"PATCH\",\"path\":\"/v1/x\","
                + "\"changes\":[{\"section\":\"name\",\"before\":\"a\",\"after\":\"b\"}],\"note\":\"n\"}");
        var plan = atlas.dryRun("patch", "/v1/x", Map.of("name", "b"));
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/x", mock.path);
        assertEquals("dry_run=true", mock.rawQuery);
        assertTrue(plan.dryRun());
        assertEquals("plan", plan.mode());
        assertEquals("name", plan.changes().get(0).get("section").asText());

        atlas.withDryRun().privacy().createRequest(new CreatePrivacyRequestBody().subjectId("u").type("access").caseId("c"), "idem-1");
        assertEquals("dry_run=true", mock.rawQuery);
        assertNull(mock.idempotencyKey);

        // a GET is never a dry run, and a normal client sends its key
        atlas.withDryRun().secretKeys().list();
        assertNull(mock.rawQuery);
        atlas.privacy().createRequest(new CreatePrivacyRequestBody().subjectId("u").type("access").caseId("c"), "idem-2");
        assertEquals("idem-2", mock.idempotencyKey);
        assertNull(mock.rawQuery);
    }

    @Test
    void traceparentIsSentOnEveryCall() {
        String tp = Traceparent.create();
        assertTrue(Traceparent.parse(tp) != null);
        atlas.withTraceparent(tp).secretKeys().list();
        assertEquals(tp, mock.header("traceparent"));
        atlas.secretKeys().list();
        assertNull(mock.header("traceparent"));
        atlas.platform().whoami();
        assertNull(mock.header("traceparent"));
        atlas.withTraceparent(tp).platform().whoami();
        assertEquals(tp, mock.header("traceparent"));
    }

    @Test
    void typedResponsesDecode() {
        mock.reply(200, "{\"object\":\"secret_key\",\"id\":\"key_1\",\"kind\":\"secret_key\",\"format\":\"atlas\",\"prefix\":\"atlas_live_sk_ab\","
                + "\"scopes\":[\"read_only\"],\"resource\":{\"type\":\"instance\",\"id\":\"ins_1\"},\"status\":\"active\",\"secret\":\"atlas_live_sk_abcdef\","
                + "\"replaces\":\"key_0\",\"previous_key_expires_at\":99,\"created_at\":5,\"allowed_ips\":[\"10.0.0.0/8\"]}");
        var key = atlas.secretKeys().rotate("key_1");
        assertEquals("atlas_live_sk_abcdef", key.secret());
        assertEquals("key_0", key.replaces());
        assertEquals(99L, key.previousKeyExpiresAt());
        assertEquals("ins_1", key.resource().id());
        assertEquals(List.of("10.0.0.0/8"), key.allowedIps());

        mock.reply(200, "{\"object\":\"whoami\",\"key\":{\"id\":\"key_1\",\"type\":\"secret\",\"scopes\":[\"a\"],\"effective_scopes\":[\"a\",\"b\"],\"last_used_ip\":\"1.2.3.4\"},"
                + "\"instance\":{\"id\":\"ins_1\",\"issuer\":\"https://i\",\"jwks_uri\":\"https://i/jwks\",\"discovery_url\":\"https://i/.well-known/openid-configuration\"},\"account\":{\"id\":\"acct_1\"}}");
        var who = atlas.whoami();
        assertEquals(List.of("a", "b"), who.key().effectiveScopes());
        assertEquals("https://i/jwks", who.instance().jwksUri());
        assertEquals("acct_1", who.account().get("id").asText());

        mock.reply(200, "{\"object\":\"webhook_test\",\"delivery_id\":\"d1\",\"event_id\":\"e1\",\"status\":\"success\",\"http_status\":200,\"latency_ms\":12,\"will_retry\":false,\"signature_header_sent\":\"v1,abc v2,def\"}");
        var test = atlas.webhooks().endpoints().testNow("we_1", null);
        assertEquals("wait=10s", mock.rawQuery);
        assertEquals(12L, test.latencyMs());
        assertEquals("v1,abc v2,def", test.signatureHeaderSent());

        mock.reply(200, "{\"object\":\"webhook_replay\",\"endpoint_id\":\"we_1\",\"queued\":3,\"events_considered\":9,\"has_more\":true,\"next_since\":77,\"retention\":{\"replay_window_days\":30}}");
        var bulk = atlas.webhooks().endpoints().replay("we_1", new WebhookWindowBody().since(1L));
        assertEquals(3, bulk.queued());
        assertTrue(bulk.hasMore());
        assertEquals(77L, bulk.nextSince());

        mock.reply(200, "{\"object\":\"webhook_endpoint\",\"id\":\"we_1\",\"url\":\"https://x\",\"enabled_events\":[\"*\"],\"active\":true,\"created_at\":1,"
                + "\"health\":\"failing\",\"success_rate_24h\":0.5,\"attempts_24h\":{\"succeeded\":1,\"failed\":1},\"signature_versions\":[\"v1\",\"v2\"],"
                + "\"event_naming\":\"canonical\",\"metadata\":{\"product\":\"x\"},\"external_ref\":\"r1\",\"idempotent_replay\":true}");
        var ep = atlas.webhooks().endpoints().get("we_1");
        assertEquals("failing", ep.health());
        assertEquals(0.5, ep.successRate24h());
        assertEquals(1, ep.attempts24h().get("failed").asInt());
        assertEquals(List.of("v1", "v2"), ep.signatureVersions());
        assertEquals("r1", ep.externalRef());
        assertTrue(ep.idempotentReplay());

        mock.reply(200, "{\"object\":\"privacy_request\",\"id\":\"pr_1\",\"type\":\"deletion\",\"canonicalType\":\"deletion\",\"requestedType\":\"erasure\",\"status\":\"scheduled\","
                + "\"subjectId\":\"u1\",\"caseId\":\"c1\",\"executeAfter\":\"2026-11-01T00:00:00Z\",\"eta\":\"2026-11-01T00:00:00Z\",\"createdAt\":\"2026-10-24T00:00:00Z\"}");
        var pr = atlas.privacy().getRequest("pr_1");
        assertEquals("erasure", pr.requestedType());
        assertEquals("u1", pr.subjectId());
        assertEquals("2026-11-01T00:00:00Z", pr.eta());

        mock.reply(200, "{\"object\":\"delivery_health_list\",\"data\":[{\"kind\":\"webhook_endpoint\",\"target_id\":\"we_1\",\"state\":\"failing\",\"consecutive_failures\":4,\"success_rate_24h\":0.25}],\"has_more\":false,\"unhealthy_count\":1}");
        var health = atlas.delivery().health();
        assertEquals(1, health.unhealthyCount());
        assertEquals("we_1", health.data().get(0).targetId());
        assertEquals(0.25, health.data().get(0).successRate24h());

        mock.reply(200, "{\"object\":\"egress_ips\",\"static\":true,\"ipv4\":[\"203.0.113.5\"],\"ipv6\":[],\"applies_to\":[\"webhooks\"],\"note\":\"n\"}");
        var ips = atlas.delivery().egressIps();
        assertTrue(ips.staticAddresses());
        assertEquals(List.of("203.0.113.5"), ips.ipv4());

        mock.reply(200, "{\"object\":\"partner_link\",\"id\":\"pl_1\",\"client_id\":\"c\",\"partner\":\"cookiemunch\",\"preset\":true,\"calls_30d\":4,\"client_secret\":\"atlas_live_pl_x\",\"status\":\"active\"}");
        var pl = atlas.partnerLinks().rotateSecret("pl_1");
        assertEquals(4, pl.calls30d());
        assertEquals("atlas_live_pl_x", pl.clientSecret());

        mock.reply(200, "{\"object\":\"integration_token\",\"integration\":\"chatly\",\"product\":\"chatly\",\"jwt\":\"a.b.c\",\"expires_in\":120,\"audience\":\"chatly:ch_1\"}");
        var tok = atlas.sessions().getIntegrationToken("sess_1", "chatly");
        assertEquals("a.b.c", tok.jwt());
        assertEquals(120, tok.expiresIn());
    }

    @Test
    void provisioningFieldsOnExistingModels() {
        mock.reply(200, "{\"object\":\"platform_app\",\"id\":\"app_1\",\"name\":\"A\",\"metadata\":{\"product\":\"x\"},\"external_ref\":\"ref-1\",\"idempotent_replay\":true}");
        var app = atlas.platform().apps().create(new CreatePlatformAppBody().name("A").externalRef("ref-1"));
        assertEquals("ref-1", app.externalRef());
        assertEquals("x", app.metadata().get("product"));
        assertTrue(app.idempotentReplay());

        mock.reply(200, "{\"object\":\"oauth_client\",\"id\":\"oac_1\",\"client_secret\":\"s\",\"external_ref\":\"r\",\"idempotent_replay\":false}");
        var client = atlas.oauthClients().create(new CreateOAuthClientBody("A", List.of("https://a/cb")));
        assertEquals("r", client.externalRef());

        mock.reply(200, "{\"object\":\"preflight\",\"ok\":true,\"checked_at\":1,\"min_severity\":\"warning\",\"findings\":[{\"severity\":\"warning\",\"code\":\"pkce_not_required\",\"message\":\"m\",\"fix\":\"f\",\"group\":\"security_posture\"}],"
                + "\"suppressed\":[{\"code\":\"x\"}],\"integrations\":[{\"origin\":\"https://a\",\"integration\":\"redirect\"}]}");
        var pf = atlas.instance().preflight("warning", null);
        assertEquals("security_posture", pf.findings().get(0).group());
        assertEquals("warning", pf.minSeverity());
        assertEquals(1, pf.suppressed().size());
        assertEquals("redirect", pf.integrations().get(0).get("integration").asText());
    }

    @Test
    void errorCodesAndHelpers() {
        mock.reply(403, "{\"errors\":[{\"code\":\"INSUFFICIENT_SCOPE\",\"message\":\"no\",\"meta\":{\"required_scope\":\"webhooks:reveal\",\"legacy_code\":\"SCOPE_MISSING\"},\"docs_url\":\"https://atlasauth.net/errors/INSUFFICIENT_SCOPE\"}]}");
        AtlasApiException e = org.junit.jupiter.api.Assertions.assertThrows(AtlasApiException.class, () -> atlas.secretKeys().list());
        assertTrue(e.isInsufficientScope());
        assertEquals("webhooks:reveal", e.requiredScope());
        assertEquals("https://atlasauth.net/errors/INSUFFICIENT_SCOPE", e.errors().get(0).docsUrl());

        mock.reply(403, "{\"errors\":[{\"code\":\"SCOPE_MISSING\",\"message\":\"no\",\"meta\":{\"required_scope\":\"apps:write\"}}]}");
        AtlasApiException old = org.junit.jupiter.api.Assertions.assertThrows(AtlasApiException.class, () -> atlas.secretKeys().list());
        assertTrue(old.isInsufficientScope());
        assertEquals("apps:write", old.requiredScope());

        mock.reply(403, "{\"errors\":[{\"code\":\"STEP_UP_REQUIRED\",\"message\":\"mfa\"}]}");
        assertTrue(org.junit.jupiter.api.Assertions.assertThrows(AtlasApiException.class, () -> atlas.platform().keys().list()).isStepUpRequired());

        mock.reply(404, "{\"errors\":[{\"code\":\"ROUTE_NOT_FOUND\",\"message\":\"no route\",\"did_you_mean\":[\"/v1/users\"],\"allowed_methods\":[\"GET\"]}]}");
        AtlasApiException nf = org.junit.jupiter.api.Assertions.assertThrows(AtlasApiException.class, () -> atlas.users().list());
        assertTrue(nf.isRouteNotFound());
        assertEquals(List.of("/v1/users"), nf.didYouMean());
        assertEquals(List.of("GET"), nf.errors().get(0).allowedMethods());
        assertEquals("STEP_UP_REQUIRED", ErrorCodes.STEP_UP_REQUIRED);
        assertEquals("INSUFFICIENT_SCOPE", ErrorCodes.INSUFFICIENT_SCOPE);
    }

    @Test
    void emptyDeleteBodyAndNullsDoNotLeak() throws Exception {
        // a clear() sends an explicit null; an unset field is omitted
        atlas.secretKeys().update("key_1", new UpdateSecretKeyBody().clear("expires_at"));
        JsonNode body = om.readTree(mock.requestBody);
        assertTrue(body.has("expires_at") && body.get("expires_at").isNull());
        assertEquals(1, body.size());
    }
}
