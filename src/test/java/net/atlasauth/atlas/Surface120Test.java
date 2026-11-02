package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.BrandingSettings;
import net.atlasauth.atlas.models.CookieConsentBody;
import net.atlasauth.atlas.models.CreateOrganizationBody;
import net.atlasauth.atlas.models.CreateScimTokenBody;
import net.atlasauth.atlas.models.CreateSsoConnectionBody;
import net.atlasauth.atlas.models.CreateSsoOnboardingProfileBody;
import net.atlasauth.atlas.models.CreateSsoOnboardingTicketBody;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.CreateWebhookEndpointBody;
import net.atlasauth.atlas.models.CustomDomain;
import net.atlasauth.atlas.models.ImportExportImportUsersBody;
import net.atlasauth.atlas.models.ImportExportUserRow;
import net.atlasauth.atlas.models.InitialSecret;
import net.atlasauth.atlas.models.IssuedSsoOnboardingTicket;
import net.atlasauth.atlas.models.JwtConnection;
import net.atlasauth.atlas.models.JwtConnectionBody;
import net.atlasauth.atlas.models.ListOrganizationsParams;
import net.atlasauth.atlas.models.OrgDomain;
import net.atlasauth.atlas.models.OrgLdapConnection;
import net.atlasauth.atlas.models.OrgLdapConnectionBody;
import net.atlasauth.atlas.models.Organization;
import net.atlasauth.atlas.models.PlatformAppProvisioned;
import net.atlasauth.atlas.models.RotateWebhookSecretBody;
import net.atlasauth.atlas.models.ScimTokenWithSecret;
import net.atlasauth.atlas.models.SsoConnection;
import net.atlasauth.atlas.models.UpdateOrganizationBody;
import net.atlasauth.atlas.models.UpdateReferralSettingsBody;
import net.atlasauth.atlas.models.WebhookTestResult;
import net.atlasauth.atlas.webhook.WebhookEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Request shaping and response decoding for the 1.20 surface (organizations, SSO, trusted-JWT, webhooks, platform, domains). */
class Surface120Test {

    private static final ObjectMapper JSON = new ObjectMapper();

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        mock.reply(200, "{}");
        atlas = AtlasClient.builder()
                .secretKey("sk_test_123")
                .platformKey("psk_test_456")
                .apiUrl(mock.baseUrl())
                .build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private void expect(String method, String path, String query, String body, Consumer<AtlasClient> call) throws IOException {
        mock.requestBody = null;
        call.accept(atlas);
        assertEquals(method, mock.method, path);
        assertEquals(path, mock.path);
        assertEquals(query, mock.rawQuery == null ? null : URLDecoder.decode(mock.rawQuery, StandardCharsets.UTF_8), path + " query");
        if (body == null) {
            assertTrue(mock.requestBody == null || mock.requestBody.isEmpty(), path + " should send no body");
        } else {
            assertEquals(JSON.readTree(body), JSON.readTree(mock.requestBody), path + " body");
        }
    }

    @Test
    void organizationsCreateListFindAndPolicy() throws IOException {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("plan", "pro");
        meta.put("region", "eu");
        expect("POST", "/v1/organizations", null,
                "{\"name\":\"Acme\",\"slug\":\"acme\",\"external_id\":\"cus_1\",\"public_metadata\":{\"plan\":\"pro\"},\"owner_email\":\"owner@example.com\",\"role_mappings\":{\"org:admin\":\"owner\"}}",
                c -> c.organizations().create(new CreateOrganizationBody().name("Acme").slug("acme").externalId("cus_1")
                        .publicMetadata(Map.of("plan", "pro")).ownerEmail("owner@example.com").roleMappings(Map.of("org:admin", "owner"))));
        expect("PATCH", "/v1/organizations/org_1", null, "{\"external_id\":null,\"role_mappings\":{\"org:member\":\"viewer\"}}",
                c -> c.organizations().update("org_1", new UpdateOrganizationBody().roleMappings(Map.of("org:member", "viewer")).clear("external_id")));
        expect("GET", "/v1/organizations", "external_id=cus_1&metadata[plan]=pro&metadata[region]=eu", null,
                c -> c.organizations().list(new ListOrganizationsParams().externalId("cus_1").metadata(meta)));
        expect("GET", "/v1/organizations/by_domain/user@example.com", null, null, c -> c.organizations().getByDomain("user@example.com"));
        expect("GET", "/v1/organizations/by_domain/example.com", "include_unverified=true", null, c -> c.organizations().getByDomain("example.com", true));
        expect("GET", "/v1/organizations/org_1/policy", null, null, c -> c.organizations().getPolicy("org_1"));
        expect("POST", "/v1/organizations/org_1/domains/dom_1/verify", null, null, c -> c.organizations().domains().verify("org_1", "dom_1"));
        expect("POST", "/v1/organizations/org_1/domains/dom_1/verify", null, "{\"method\":\"email\",\"code\":\"123456\"}",
                c -> c.organizations().domains().verify("org_1", "dom_1", "email", "123456"));
        expect("POST", "/v1/organizations/org_1/domains/dom_1/verification_email", null, "{\"address\":\"hostmaster\"}",
                c -> c.organizations().domains().sendVerificationEmail("org_1", "dom_1", "hostmaster"));
    }

    @Test
    void organizationDecodesSsoOwnerInvitationAndMatchedDomain() {
        mock.reply(200, "{\"object\":\"organization\",\"id\":\"org_1\",\"name\":\"Acme\",\"created_by\":null,\"external_id\":\"cus_1\","
                + "\"role_mappings\":{\"org:admin\":\"owner\"},"
                + "\"owner_invitation\":{\"object\":\"organization_invitation\",\"id\":\"orginv_1\",\"email\":\"owner@example.com\",\"role\":\"org:admin\",\"status\":\"pending\",\"expires_at\":1700000000000},"
                + "\"sso\":{\"connections\":[{\"id\":\"sso_1\",\"type\":\"saml\",\"status\":\"active\"}],\"scim_base_url\":\"https://auth.example.com/scim/v2\"},"
                + "\"domain\":{\"object\":\"org_domain\",\"id\":\"dom_1\",\"domain\":\"example.com\",\"status\":\"verified\",\"verified_at\":1}}");
        Organization org = atlas.organizations().get("org_1");
        assertNull(org.createdBy());
        assertEquals("cus_1", org.externalId());
        assertEquals("owner", org.roleMappings().get("org:admin"));
        assertEquals("owner@example.com", org.ownerInvitation().email());
        assertEquals("active", org.sso().connections().get(0).status());
        assertEquals("https://auth.example.com/scim/v2", org.sso().scimBaseUrl());
        assertEquals("verified", org.domain().status());

        mock.reply(200, "{\"object\":\"org_domain\",\"id\":\"dom_1\",\"status\":\"pending\",\"verification_method\":null,\"sent_to\":\"admin@example.com\","
                + "\"verification_methods\":{\"dns_txt\":{\"record_name\":\"_atlas.example.com\",\"record_type\":\"TXT\",\"record_value\":\"v\"},"
                + "\"well_known\":{\"url\":\"https://example.com/.well-known/atlas\",\"body\":\"v\",\"content_type\":\"text/plain\"},"
                + "\"email\":{\"addresses\":[\"admin@example.com\"],\"sent_to\":\"admin@example.com\",\"expires_at\":2}}}");
        OrgDomain d = atlas.organizations().domains().sendVerificationEmail("org_1", "dom_1", null);
        assertEquals("admin@example.com", d.sentTo());
        assertEquals("https://example.com/.well-known/atlas", d.verificationMethods().wellKnown().url());
        assertEquals(List.of("admin@example.com"), d.verificationMethods().email().addresses());

        mock.reply(201, "{\"object\":\"scim_token\",\"id\":\"st_1\",\"secret\":\"s\",\"scim_base_url\":\"https://auth.example.com/scim/v2\","
                + "\"warnings\":[{\"code\":\"no_sso_connection\",\"message\":\"m\"}]}");
        ScimTokenWithSecret tok = atlas.scimTokens().create(new CreateScimTokenBody("org_1"));
        assertEquals("https://auth.example.com/scim/v2", tok.scimBaseUrl());
        assertEquals("no_sso_connection", tok.warnings().get(0).code());
    }

    @Test
    void ssoConnectionsLdapAndOnboarding() throws IOException {
        expect("POST", "/v1/sso_connections", null,
                "{\"organization_id\":\"org_1\",\"type\":\"oidc\",\"saml_acs_path\":\"/saml/acs/acme\",\"github_access\":{\"allowed_organizations\":[\"acme\"],\"allowed_teams\":[\"acme/eng\"]}}",
                c -> c.ssoConnections().create(new CreateSsoConnectionBody().organizationId("org_1").type("oidc").samlAcsPath("/saml/acs/acme")
                        .githubAccess(new SsoConnection.GithubAccess(List.of("acme"), List.of("acme/eng")))));
        expect("GET", "/v1/sso_onboarding_profiles", "name=Acme setup", null, c -> c.ssoOnboarding().list("Acme setup"));
        expect("POST", "/v1/sso_onboarding_profiles", null, "{\"name\":\"Acme setup\",\"return_url\":\"https://app.example.com/done\"}",
                c -> c.ssoOnboarding().create(new CreateSsoOnboardingProfileBody("Acme setup").returnUrl("https://app.example.com/done")));
        expect("POST", "/v1/sso_onboarding_profiles/ssop_1/tickets", null, "{\"organization_id\":\"org_1\",\"expires_in_seconds\":600,\"return_url\":\"https://app.example.com/x\"}",
                c -> c.ssoOnboarding().createTicket("ssop_1", new CreateSsoOnboardingTicketBody().organizationId("org_1").expiresInSeconds(600L).returnUrl("https://app.example.com/x")));

        mock.reply(201, "{\"object\":\"sso_onboarding_ticket\",\"id\":\"ssot_1\",\"return_url\":null,\"effective_return_url\":\"https://app.example.com/done\",\"expires_in\":600}");
        IssuedSsoOnboardingTicket ticket = atlas.ssoOnboarding().createTicket("ssop_1");
        assertNull(ticket.returnUrl());
        assertEquals("https://app.example.com/done", ticket.effectiveReturnUrl());

        mock.reply(200, "{\"object\":\"sso_connection\",\"id\":\"sso_1\",\"saml_acs_path\":\"/saml/acs/acme\",\"github_access\":{\"allowed_organizations\":[\"acme\"],\"allowed_teams\":[]}}");
        SsoConnection conn = atlas.ssoConnections().get("sso_1");
        assertEquals("/saml/acs/acme", conn.samlAcsPath());
        assertEquals(List.of("acme"), conn.githubAccess().allowedOrganizations());

        String ldapJson = JSON.writeValueAsString(JSON.readTree(net.atlasauth.atlas.http.Json.mapper().writeValueAsString(
                new OrgLdapConnectionBody().groupRoleMappings(List.of(new OrgLdapConnection.GroupRoleMapping("cn=admins,dc=example,dc=com", "org:admin"))))));
        assertEquals("{\"group_role_mappings\":[{\"group\":\"cn=admins,dc=example,dc=com\",\"role\":\"org:admin\"}]}", ldapJson);
    }

    @Test
    void jwtConnectionsCrud() throws IOException {
        expect("GET", "/v1/jwt_connections", null, null, c -> c.jwtConnections().list());
        expect("GET", "/v1/jwt_connections/jwtc_1", null, null, c -> c.jwtConnections().get("jwtc_1"));
        expect("POST", "/v1/jwt_connections", null,
                "{\"organization_id\":\"org_1\",\"jwks_uri\":\"https://idp.example.com/jwks\",\"issuer\":\"https://idp.example.com\",\"max_age_seconds\":120}",
                c -> c.jwtConnections().create(new JwtConnectionBody().organizationId("org_1").jwksUri("https://idp.example.com/jwks")
                        .issuer("https://idp.example.com").maxAgeSeconds(120)));
        expect("PATCH", "/v1/jwt_connections/jwtc_1", null, "{\"status\":\"active\",\"default_role_id\":null}",
                c -> c.jwtConnections().update("jwtc_1", new JwtConnectionBody().status("active").clear("default_role_id")));
        expect("DELETE", "/v1/jwt_connections/jwtc_1", null, null, c -> c.jwtConnections().delete("jwtc_1"));

        mock.reply(200, "{\"object\":\"jwt_connection\",\"id\":\"jwtc_1\",\"organization_id\":null,\"status\":\"draft\",\"email_claim\":\"email\","
                + "\"max_age_seconds\":300,\"login_url\":\"https://auth.example.com/v1/jwt/jwtc_1\",\"created_at\":1,\"updated_at\":2}");
        JwtConnection jc = atlas.jwtConnections().get("jwtc_1");
        assertNull(jc.organizationId());
        assertEquals(300, jc.maxAgeSeconds());
        assertEquals("https://auth.example.com/v1/jwt/jwtc_1", jc.loginUrl());
    }

    @Test
    void webhooksChosenSecretAndTestResult() throws IOException {
        String secret = "whsec_" + "a".repeat(32);
        expect("POST", "/v1/webhook_endpoints", null, "{\"url\":\"https://hooks.example.com/a\",\"active\":false,\"secret\":\"" + secret + "\"}",
                c -> c.webhooks().endpoints().create(new CreateWebhookEndpointBody("https://hooks.example.com/a").active(false).secret(secret)));
        expect("POST", "/v1/webhook_endpoints/we_1/rotate_secret", null, "{\"overlap_seconds\":60,\"secret\":\"" + secret + "\"}",
                c -> c.webhooks().endpoints().rotateSecret("we_1", new RotateWebhookSecretBody().overlapSeconds(60).secret(secret)));

        mock.reply(200, "{\"object\":\"webhook_test_result\",\"delivery_id\":\"wd_1\",\"event_type\":\"webhook.test\",\"status\":\"failed\",\"error_kind\":\"timeout\",\"will_retry\":false}");
        WebhookTestResult r = atlas.webhooks().endpoints().testNow("we_1", "10s");
        assertEquals(WebhookEvents.WEBHOOK_TEST, r.eventType());
        assertEquals("timeout", r.errorKind());

        assertEquals("scim_group.member_added", WebhookEvents.SCIM_GROUP_MEMBER_ADDED);
        assertEquals("sso_onboarding_ticket.completed", WebhookEvents.SSO_ONBOARDING_TICKET_COMPLETED);
    }

    @Test
    void usersImportsAndInstanceSettings() throws IOException {
        expect("POST", "/v1/users", null,
                "{\"email_address\":\"a@example.com\",\"password_hash\":\"h\",\"password_hash_algorithm\":\"scrypt\",\"password_hash_params\":{\"n\":16384},\"password_salt\":\"s\"}",
                c -> c.users().create(new CreateUserBody("a@example.com").passwordHash("h").passwordHashAlgorithm("scrypt")
                        .passwordHashParams(Map.of("n", 16384)).passwordSalt("s")));
        expect("POST", "/v1/user_imports", null,
                "{\"users\":[{\"email_address\":\"a@example.com\",\"password_hash\":\"h\",\"password_hash_algorithm\":\"sha256_salted\",\"password_hash_params\":{\"salt_position\":\"prefix\"},\"password_salt\":\"s\"}]}",
                c -> c.importExport().importUsers(new ImportExportImportUsersBody(List.of(new ImportExportUserRow().emailAddress("a@example.com")
                        .passwordHash("h").passwordHashAlgorithm("sha256_salted").passwordHashParams(Map.of("salt_position", "prefix")).passwordSalt("s")))));
        expect("PATCH", "/v1/instance/referrals", null, "{\"partner_portal_url\":\"https://partners.example.com\",\"report_linked_atlas_id_signups\":true}",
                c -> c.referrals().update(new UpdateReferralSettingsBody().partnerPortalUrl("https://partners.example.com").reportLinkedAtlasIdSignups(true)));
        expect("PUT", "/v1/instance/cookie_consent", null, "{\"host_aliases\":[\"acme-fapi.example.com\"]}",
                c -> c.instance().replaceCookieConsent(new CookieConsentBody().hostAliases(List.of("acme-fapi.example.com"))));
        Map<String, Boolean> panels = new LinkedHashMap<>();
        panels.put("referrals", false);
        panels.put("ciba", null);
        expect("PATCH", "/v1/branding", null, "{\"accountPortal\":{\"panels\":{\"referrals\":false,\"ciba\":null}}}",
                c -> c.branding().update(new BrandingSettings().accountPortal(new BrandingSettings.AccountPortal().panels(panels))));
    }

    @Test
    void platformInitialSecretAndPathMountDomains() throws IOException {
        expect("POST", "/v1/platform/apps/app_1/initial_secret/reveal", null, null, c -> c.platform().apps().revealInitialSecret("app_1"));
        expect("POST", "/v1/platform/apps/app_1/initial_secret/reveal", "dry_run=true", null, c -> c.platform().apps().revealInitialSecret("app_1", true));
        expect("POST", "/v1/domains", null, "{\"method\":\"path\",\"hostname\":\"app.example.com\",\"base_path\":\"/__atlas\"}",
                c -> c.domains().createPathMount("app.example.com", "/__atlas", null));

        mock.reply(200, "{\"object\":\"initial_secret\",\"app_id\":\"app_1\",\"secret\":\"sk_live_x\",\"sibling_instance\":{\"instance_id\":\"ins_2\",\"key_id\":\"k2\",\"secret\":null}}");
        InitialSecret s = atlas.platform().apps().revealInitialSecret("app_1");
        assertEquals("sk_live_x", s.secret());
        assertNull(s.siblingInstance().secret());

        mock.reply(200, "{\"object\":\"platform_app\",\"id\":\"app_1\",\"secret\":null,\"secret_redacted\":true,"
                + "\"initial_secret_reveal\":{\"path\":\"/v1/platform/apps/app_1/initial_secret/reveal\",\"expires_at\":5}}");
        PlatformAppProvisioned p = atlas.platform().apps().create(new net.atlasauth.atlas.models.CreatePlatformAppBody().name("acme"), "idem_1");
        assertEquals(Boolean.TRUE, p.secretRedacted());
        assertEquals(5L, p.initialSecretReveal().expiresAt());

        mock.reply(201, "{\"object\":\"custom_domain\",\"id\":\"cd_1\",\"host\":\"app.example.com\",\"method\":\"path\",\"cname_target\":null,\"base_path\":\"/__atlas\","
                + "\"frontend_api_url\":\"https://app.example.com/__atlas\",\"verification\":{\"method\":\"https_file\",\"url\":\"https://app.example.com/.well-known/atlas-verify/t\",\"token\":\"t\",\"content\":\"t\",\"instructions\":\"i\"}}");
        CustomDomain d = atlas.domains().createPathMount("app.example.com", null, null);
        assertEquals("path", d.method());
        assertNull(d.cnameTarget());
        assertEquals("https_file", d.verification().method());
        assertEquals("https://app.example.com/__atlas", d.frontendApiUrl());
    }
}
