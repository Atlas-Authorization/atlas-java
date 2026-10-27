package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.AbacAccessibleResourcesBody;
import net.atlasauth.atlas.models.AbacAuthzenEvaluationBody;
import net.atlasauth.atlas.models.AbacAuthzenSearchActionBody;
import net.atlasauth.atlas.models.AbacAuthzenSearchResourceBody;
import net.atlasauth.atlas.models.AbacAuthzenSearchSubjectBody;
import net.atlasauth.atlas.models.AbacCreateBody;
import net.atlasauth.atlas.models.AbacDiffSourcesBody;
import net.atlasauth.atlas.models.AbacEvaluateBody;
import net.atlasauth.atlas.models.AbacLinksCreateBody;
import net.atlasauth.atlas.models.AbacLinksUpdateBody;
import net.atlasauth.atlas.models.AbacPartialEvaluateBody;
import net.atlasauth.atlas.models.AbacSchemaSetBody;
import net.atlasauth.atlas.models.AbacSchemaValidateBody;
import net.atlasauth.atlas.models.AbacTemplatesCreateBody;
import net.atlasauth.atlas.models.AbacTemplatesUpdateBody;
import net.atlasauth.atlas.models.AbacTestBody;
import net.atlasauth.atlas.models.AbacUpdateBody;
import net.atlasauth.atlas.models.AccessPoliciesCreateBody;
import net.atlasauth.atlas.models.AccessPoliciesSimulateBody;
import net.atlasauth.atlas.models.AgentAccessRequestsApproveBody;
import net.atlasauth.atlas.models.AgentAccessRequestsDenyBody;
import net.atlasauth.atlas.models.AgentRegistrationReplaceBody;
import net.atlasauth.atlas.models.AgentsClaimBody;
import net.atlasauth.atlas.models.AgentsRevokeAccessBody;
import net.atlasauth.atlas.models.AgentsSetOwnerBody;
import net.atlasauth.atlas.models.AnalyticsAlertRulesCreateBody;
import net.atlasauth.atlas.models.AnalyticsAlertRulesTestBody;
import net.atlasauth.atlas.models.AnalyticsAlertRulesUpdateBody;
import net.atlasauth.atlas.models.AppCatalogInstallBody;
import net.atlasauth.atlas.models.AppCatalogUpdateBody;
import net.atlasauth.atlas.models.AttackProtectionSetAppAttestationSecretBody;
import net.atlasauth.atlas.models.AuditEventSchemasRedactBody;
import net.atlasauth.atlas.models.AuditEventSchemasRegisterBody;
import net.atlasauth.atlas.models.AuditEventSchemasUpdateBody;
import net.atlasauth.atlas.models.AuthzDecisionsSettingsUpdateBody;
import net.atlasauth.atlas.models.ConnectionsConnectBody;
import net.atlasauth.atlas.models.ConnectionsCreateBody;
import net.atlasauth.atlas.models.ConnectionsExportConnectionsBody;
import net.atlasauth.atlas.models.ConnectionsImportConnectionsBody;
import net.atlasauth.atlas.models.ConnectionsOauth1SignBody;
import net.atlasauth.atlas.models.ConnectionsOauthClientsSetBody;
import net.atlasauth.atlas.models.ConnectionsRefreshTokenBody;
import net.atlasauth.atlas.models.ConnectionsRelayBody;
import net.atlasauth.atlas.models.ConnectionsRotateCredentialsBody;
import net.atlasauth.atlas.models.ConnectionsSetCredentialsBody;
import net.atlasauth.atlas.models.ConnectionsTokensBody;
import net.atlasauth.atlas.models.ConnectionsUpdateBody;
import net.atlasauth.atlas.models.ConnectionsWebhooksCreateEndpointBody;
import net.atlasauth.atlas.models.ConnectionsWebhooksUpdateEndpointBody;
import net.atlasauth.atlas.models.ConnectionsWebhooksVerifyBody;
import net.atlasauth.atlas.models.DesktopMfaCreateEnrolmentCodeBody;
import net.atlasauth.atlas.models.DesktopMfaUpdatePolicyBody;
import net.atlasauth.atlas.models.DevicesIngestPostureBody;
import net.atlasauth.atlas.models.DevicesReportBody;
import net.atlasauth.atlas.models.DirectoryPullSourcesCreateBody;
import net.atlasauth.atlas.models.DirectoryPullSourcesSetCsvBody;
import net.atlasauth.atlas.models.DirectoryPullSourcesUpdateBody;
import net.atlasauth.atlas.models.FgaListUsersBody;
import net.atlasauth.atlas.models.FlagsAssignOrganizationEntitlementsBody;
import net.atlasauth.atlas.models.FlagsCreateBody;
import net.atlasauth.atlas.models.FlagsEvaluateBody;
import net.atlasauth.atlas.models.FlagsUpdateBody;
import net.atlasauth.atlas.models.GroupsAddMembersBody;
import net.atlasauth.atlas.models.GroupsCreateBody;
import net.atlasauth.atlas.models.GroupsRulesCreateBody;
import net.atlasauth.atlas.models.GroupsRulesUpdateBody;
import net.atlasauth.atlas.models.GroupsUpdateBody;
import net.atlasauth.atlas.models.IdentityVerificationDecideBody;
import net.atlasauth.atlas.models.IdentityVerificationStartBody;
import net.atlasauth.atlas.models.IdentityVerificationUpdateSettingsBody;
import net.atlasauth.atlas.models.IgaCampaignsCreateBody;
import net.atlasauth.atlas.models.IgaCampaignsDecideBody;
import net.atlasauth.atlas.models.IgaDelegationsCreateBody;
import net.atlasauth.atlas.models.IgaEntitlementsCreateBody;
import net.atlasauth.atlas.models.IgaEntitlementsUpdateBody;
import net.atlasauth.atlas.models.IgaGrantsCreateBody;
import net.atlasauth.atlas.models.IgaGrantsRevokeBody;
import net.atlasauth.atlas.models.IgaLifecycleCreateBirthrightRuleBody;
import net.atlasauth.atlas.models.IgaLifecycleOffboardBody;
import net.atlasauth.atlas.models.IgaLifecycleUpdateBirthrightRuleBody;
import net.atlasauth.atlas.models.IgaLifecycleUpdateSettingsBody;
import net.atlasauth.atlas.models.IgaRequestsCreateBody;
import net.atlasauth.atlas.models.IgaRequestsDecideBody;
import net.atlasauth.atlas.models.IgaSodCreateRuleBody;
import net.atlasauth.atlas.models.IgaSodUpdateRuleBody;
import net.atlasauth.atlas.models.InstanceIntegrationsRotateEventsSecretBody;
import net.atlasauth.atlas.models.IntegrationsRunCardActionBody;
import net.atlasauth.atlas.models.JourneysActivateBody;
import net.atlasauth.atlas.models.JourneysCreateBody;
import net.atlasauth.atlas.models.JourneysSimulateBody;
import net.atlasauth.atlas.models.JourneysUpdateBody;
import net.atlasauth.atlas.models.LdapServerClientsCreateBody;
import net.atlasauth.atlas.models.LdapServerClientsUpdateBody;
import net.atlasauth.atlas.models.LeastPrivilegeApplyBody;
import net.atlasauth.atlas.models.ListenCreateSessionBody;
import net.atlasauth.atlas.models.ManagedGrantsNarrowBody;
import net.atlasauth.atlas.models.MarketingUpdateSettingsBody;
import net.atlasauth.atlas.models.McpGatewaysBudgetsCreateBody;
import net.atlasauth.atlas.models.McpGatewaysCreateBody;
import net.atlasauth.atlas.models.McpGatewaysUpdateBody;
import net.atlasauth.atlas.models.MfaFactorsSetDuoSecretBody;
import net.atlasauth.atlas.models.MfaFactorsSetSmartCardSecretBody;
import net.atlasauth.atlas.models.MfaFactorsSetYubicoSecretBody;
import net.atlasauth.atlas.models.MfaFactorsSmartCardMappingsAddBody;
import net.atlasauth.atlas.models.MfaFactorsYubikeysRegisterBody;
import net.atlasauth.atlas.models.MigrationsExchangeBody;
import net.atlasauth.atlas.models.MigrationsImportsApplyBody;
import net.atlasauth.atlas.models.MigrationsImportsCreateBody;
import net.atlasauth.atlas.models.MigrationsImportsPutPartBody;
import net.atlasauth.atlas.models.MigrationsSetBridgeBody;
import net.atlasauth.atlas.models.NetworkZonesCheckBody;
import net.atlasauth.atlas.models.NetworkZonesCreateBody;
import net.atlasauth.atlas.models.OAuthClientsAppSettingsUpdateBody;
import net.atlasauth.atlas.models.OAuthClientsConsentRequestsAcceptBody;
import net.atlasauth.atlas.models.OAuthClientsLoginRequestsAcceptBody;
import net.atlasauth.atlas.models.OAuthClientsLoginRequestsRejectBody;
import net.atlasauth.atlas.models.OAuthClientsSetDelegatedUiBody;
import net.atlasauth.atlas.models.OAuthClientsSetExchangePolicyBody;
import net.atlasauth.atlas.models.OAuthClientsSetProfileBody;
import net.atlasauth.atlas.models.OAuthClientsTrustedIssuersCreateBody;
import net.atlasauth.atlas.models.OauthClientProfilesSetBody;
import net.atlasauth.atlas.models.ObjectMapper;
import net.atlasauth.atlas.models.OpenFgaListUsersBody;
import net.atlasauth.atlas.models.OrgLdapConnectionsSetRelayBody;
import net.atlasauth.atlas.models.OrgLdapConnectionsSetSyncBody;
import net.atlasauth.atlas.models.OrgLdapConnectionsSyncRunsCreateBody;
import net.atlasauth.atlas.models.OrgRoleGrantsCreateBody;
import net.atlasauth.atlas.models.OrgRoleGrantsDelegateBody;
import net.atlasauth.atlas.models.OrgRoleGrantsUpdateBody;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsCreateBody;
import net.atlasauth.atlas.models.PageTemplatesSaveDraftBody;
import net.atlasauth.atlas.models.PasskeyApiLoginFinalizeBody;
import net.atlasauth.atlas.models.PasskeyApiLoginInitBody;
import net.atlasauth.atlas.models.PasskeyApiRegistrationFinalizeBody;
import net.atlasauth.atlas.models.PasskeyApiRegistrationInitBody;
import net.atlasauth.atlas.models.PolicyBundlesImportBody;
import net.atlasauth.atlas.models.PolicyBundlesVerifyBody;
import net.atlasauth.atlas.models.ProxyAppsCreateBody;
import net.atlasauth.atlas.models.ProxyAppsUpdateBody;
import net.atlasauth.atlas.models.RelayAgentsCreateBody;
import net.atlasauth.atlas.models.RelayAgentsUpdateBody;
import net.atlasauth.atlas.models.ResourcesAccessibleBody;
import net.atlasauth.atlas.models.ResourcesAssignmentsAssignBody;
import net.atlasauth.atlas.models.ResourcesCheckBatchBody;
import net.atlasauth.atlas.models.ResourcesCheckBody;
import net.atlasauth.atlas.models.ResourcesSyncBody;
import net.atlasauth.atlas.models.ResourcesTypesCreateBody;
import net.atlasauth.atlas.models.ResourcesTypesUpdateBody;
import net.atlasauth.atlas.models.ResourcesUpsertBody;
import net.atlasauth.atlas.models.RiskBasedMfaSetReputationSecretBody;
import net.atlasauth.atlas.models.ServiceConnectorsCreateBody;
import net.atlasauth.atlas.models.ServiceConnectorsInvokeBody;
import net.atlasauth.atlas.models.ServiceConnectorsUpdateBody;
import net.atlasauth.atlas.models.SsfReceiversCreateBody;
import net.atlasauth.atlas.models.SsfReceiversUpdateBody;
import net.atlasauth.atlas.models.SsfStreamsCreateBody;
import net.atlasauth.atlas.models.SsfStreamsUpdateBody;
import net.atlasauth.atlas.models.SsfStreamsUpdateStatusBody;
import net.atlasauth.atlas.models.SsfStreamsVerifyBody;
import net.atlasauth.atlas.models.UsersCreateLinkBody;
import net.atlasauth.atlas.models.UsersGuardiansApproveBody;
import net.atlasauth.atlas.models.UsersGuardiansRequestBody;
import net.atlasauth.atlas.models.UsersNotesCreateBody;
import net.atlasauth.atlas.models.UsersNotesUpdateBody;
import net.atlasauth.atlas.models.UsersRequiredActionsBulkBody;
import net.atlasauth.atlas.models.UsersRequiredActionsRequestBody;
import net.atlasauth.atlas.models.UsersSearchExportBody;
import net.atlasauth.atlas.models.UsersSearchQueryBody;
import net.atlasauth.atlas.models.UsersSearchViewsCreateBody;
import net.atlasauth.atlas.models.UsersSearchViewsUpdateBody;
import net.atlasauth.atlas.models.VaultCreateBody;
import net.atlasauth.atlas.models.VaultKeyProviderSetBody;
import net.atlasauth.atlas.models.VaultUpdateBody;
import net.atlasauth.atlas.models.VcCreateOfferBody;
import net.atlasauth.atlas.models.VcCredentialTypesCreateBody;
import net.atlasauth.atlas.models.VcPresentationRequestsCreateBody;
import net.atlasauth.atlas.models.WorkflowsCreateBody;
import net.atlasauth.atlas.models.WorkflowsDryRunBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Request shaping for the Backend API operations added in the parity release: verb, exact URL (encoded path parameters and query), JSON body and idempotency key. */
class ParityOperationsTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        mock.reply(200, "{}");
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private void expect(String method, String path, String query, String body, String idem, Consumer<AtlasClient> call) throws IOException {
        mock.requestBody = null;
        call.accept(atlas);
        assertEquals(method, mock.method, path);
        assertEquals(path, mock.rawPath);
        assertEquals(query, mock.rawQuery, path + " query");
        assertEquals(idem, mock.idempotencyKey, path + " idempotency key");
        if (body == null) {
            assertTrue(mock.requestBody == null || mock.requestBody.isEmpty(), path + " should send no body");
        } else {
            assertEquals(JSON.readTree(body), JSON.readTree(mock.requestBody), path + " body");
        }
    }

    @Test
    void operations00() throws IOException {
        expect("GET", "/v1/app_catalog", "limit=1", null, null, c -> c.appCatalog().presets(1L, null));
        expect("GET", "/v1/app_catalog/installed", "limit=1", null, null, c -> c.appCatalog().installed(1L, null));
        expect("POST", "/v1/app_catalog/a%20b%2Fc/install", null, "{\"installed_id\":\"s\"}", "idem-1", c -> c.appCatalog().install("a b/c", new AppCatalogInstallBody().installedId("s"), "idem-1"));
        expect("PATCH", "/v1/app_catalog/installed/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.appCatalog().update("a b/c", new AppCatalogUpdateBody().name("s")));
        expect("DELETE", "/v1/app_catalog/installed/a%20b%2Fc", null, null, null, c -> c.appCatalog().uninstall("a b/c"));
        expect("GET", "/v1/ldap_server_clients", "limit=1", null, null, c -> c.ldapServerClients().list(1L, null));
        expect("POST", "/v1/ldap_server_clients", null, "{\"name\":\"s\"}", "idem-1", c -> c.ldapServerClients().create(new LdapServerClientsCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/ldap_server_clients/a%20b%2Fc", null, null, null, c -> c.ldapServerClients().get("a b/c"));
        expect("PATCH", "/v1/ldap_server_clients/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.ldapServerClients().update("a b/c", new LdapServerClientsUpdateBody().name("s")));
        expect("DELETE", "/v1/ldap_server_clients/a%20b%2Fc", null, null, null, c -> c.ldapServerClients().delete("a b/c"));
        expect("POST", "/v1/ldap_server_clients/a%20b%2Fc/rotate_secret", null, null, "idem-1", c -> c.ldapServerClients().rotateSecret("a b/c", "idem-1"));
        expect("POST", "/v1/users/a%20b%2Fc/required_actions", null, "{\"action\":\"s\"}", "idem-1", c -> c.users().requiredActions().request("a b/c", new UsersRequiredActionsRequestBody().action("s"), "idem-1"));
        expect("GET", "/v1/users/a%20b%2Fc/required_actions", "limit=1", null, null, c -> c.users().requiredActions().list("a b/c", 1L, null));
        expect("DELETE", "/v1/users/a%20b%2Fc/required_actions/p1", null, null, null, c -> c.users().requiredActions().clear("a b/c", "p1"));
        expect("POST", "/v1/required_actions/bulk", null, "{\"action\":\"s\"}", "idem-1", c -> c.users().requiredActions().bulk(new UsersRequiredActionsBulkBody().action("s"), "idem-1"));
        expect("GET", "/v1/users/a%20b%2Fc/notes", "limit=1", null, null, c -> c.users().notes().list("a b/c", 1L, null));
        expect("POST", "/v1/users/a%20b%2Fc/notes", null, "{\"body\":\"s\"}", "idem-1", c -> c.users().notes().create("a b/c", new UsersNotesCreateBody().body("s"), "idem-1"));
        expect("PATCH", "/v1/users/a%20b%2Fc/notes/p1", null, "{\"body\":\"s\"}", null, c -> c.users().notes().update("a b/c", "p1", new UsersNotesUpdateBody().body("s")));
        expect("DELETE", "/v1/users/a%20b%2Fc/notes/p1", null, null, null, c -> c.users().notes().delete("a b/c", "p1"));
        expect("GET", "/v1/users/a%20b%2Fc/history", "limit=1", null, null, c -> c.users().history("a b/c", 1L, null, null));
        expect("GET", "/v1/users/a%20b%2Fc/minor_status", null, null, null, c -> c.users().minorStatus("a b/c"));
        expect("POST", "/v1/users/a%20b%2Fc/minor_status/assess", null, null, "idem-1", c -> c.users().assessMinor("a b/c", "idem-1"));
        expect("POST", "/v1/users/a%20b%2Fc/guardians", null, "{\"email\":\"s\"}", "idem-1", c -> c.users().guardians().request("a b/c", new UsersGuardiansRequestBody().email("s"), "idem-1"));
        expect("POST", "/v1/users/a%20b%2Fc/guardians/p1/approve", null, "{\"note\":\"s\"}", "idem-1", c -> c.users().guardians().approve("a b/c", "p1", new UsersGuardiansApproveBody().note("s"), "idem-1"));
        expect("DELETE", "/v1/users/a%20b%2Fc/guardians/p1", null, null, null, c -> c.users().guardians().revoke("a b/c", "p1"));
    }

    @Test
    void operations01() throws IOException {
        expect("GET", "/v1/flows", "limit=1", null, null, c -> c.journeys().list(1L, null));
        expect("POST", "/v1/flows", null, "{\"name\":\"s\"}", "idem-1", c -> c.journeys().create(new JourneysCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/flows/a%20b%2Fc", null, null, null, c -> c.journeys().get("a b/c"));
        expect("PATCH", "/v1/flows/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.journeys().update("a b/c", new JourneysUpdateBody().name("s")));
        expect("DELETE", "/v1/flows/a%20b%2Fc", null, null, null, c -> c.journeys().delete("a b/c"));
        expect("GET", "/v1/flows/a%20b%2Fc/versions", "limit=1", null, null, c -> c.journeys().versions("a b/c", 1L, null));
        expect("POST", "/v1/flows/a%20b%2Fc/activate", null, "{\"version\":1}", "idem-1", c -> c.journeys().activate("a b/c", new JourneysActivateBody().version(1L), "idem-1"));
        expect("POST", "/v1/flows/a%20b%2Fc/simulate", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.journeys().simulate("a b/c", new JourneysSimulateBody().userId("s"), "idem-1"));
        expect("POST", "/v1/users/search", null, "{\"query\":\"s\"}", "idem-1", c -> c.users().search().query(new UsersSearchQueryBody().query("s"), "idem-1"));
        expect("POST", "/v1/users/search/export", null, "{\"query\":\"s\"}", "idem-1", c -> c.users().search().export(new UsersSearchExportBody().query("s"), "idem-1"));
        expect("GET", "/v1/users/search/views", "limit=1", null, null, c -> c.users().search().views().list(1L, null));
        expect("POST", "/v1/users/search/views", null, "{\"name\":\"s\"}", "idem-1", c -> c.users().search().views().create(new UsersSearchViewsCreateBody().name("s"), "idem-1"));
        expect("PATCH", "/v1/users/search/views/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.users().search().views().update("a b/c", new UsersSearchViewsUpdateBody().name("s")));
        expect("DELETE", "/v1/users/search/views/a%20b%2Fc", null, null, null, c -> c.users().search().views().delete("a b/c"));
        expect("GET", "/v1/groups", "limit=1", null, null, c -> c.groups().list(1L, null));
        expect("POST", "/v1/groups", null, "{\"slug\":\"s\"}", "idem-1", c -> c.groups().create(new GroupsCreateBody().slug("s"), "idem-1"));
        expect("GET", "/v1/groups/a%20b%2Fc", null, null, null, c -> c.groups().get("a b/c"));
        expect("PATCH", "/v1/groups/a%20b%2Fc", null, "{\"slug\":\"s\"}", null, c -> c.groups().update("a b/c", new GroupsUpdateBody().slug("s")));
        expect("DELETE", "/v1/groups/a%20b%2Fc", null, null, null, c -> c.groups().delete("a b/c"));
        expect("GET", "/v1/groups/a%20b%2Fc/members", "limit=1", null, null, c -> c.groups().listMembers("a b/c", 1L, null));
        expect("POST", "/v1/groups/a%20b%2Fc/members", null, "{}", "idem-1", c -> c.groups().addMembers("a b/c", new GroupsAddMembersBody(), "idem-1"));
        expect("POST", "/v1/groups/a%20b%2Fc/members/remove", null, "{}", "idem-1", c -> c.groups().removeMembers("a b/c", new GroupsAddMembersBody(), "idem-1"));
        expect("POST", "/v1/groups/a%20b%2Fc/reapply", null, null, "idem-1", c -> c.groups().reapply("a b/c", "idem-1"));
        expect("GET", "/v1/users/a%20b%2Fc/groups", "limit=1", null, null, c -> c.groups().forUser("a b/c", 1L, null));
        expect("GET", "/v1/group_rules", "limit=1", null, null, c -> c.groups().rules().list(1L, null));
    }

    @Test
    void operations02() throws IOException {
        expect("POST", "/v1/group_rules", null, "{\"name\":\"s\"}", "idem-1", c -> c.groups().rules().create(new GroupsRulesCreateBody().name("s"), "idem-1"));
        expect("POST", "/v1/group_rules/apply", null, null, "idem-1", c -> c.groups().rules().applyAll("idem-1"));
        expect("GET", "/v1/group_rules/a%20b%2Fc", null, null, null, c -> c.groups().rules().get("a b/c"));
        expect("PATCH", "/v1/group_rules/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.groups().rules().update("a b/c", new GroupsRulesUpdateBody().name("s")));
        expect("DELETE", "/v1/group_rules/a%20b%2Fc", null, null, null, c -> c.groups().rules().delete("a b/c"));
        expect("POST", "/v1/group_rules/a%20b%2Fc/apply", null, null, "idem-1", c -> c.groups().rules().apply("a b/c", "idem-1"));
        expect("POST", "/v1/users/a%20b%2Fc/links", null, "{\"type\":\"s\"}", "idem-1", c -> c.users().createLink("a b/c", new UsersCreateLinkBody().type("s"), "idem-1"));
        expect("GET", "/v1/users/a%20b%2Fc/device_keys", "limit=1", null, null, c -> c.users().listDeviceKeys("a b/c", 1L, null));
        expect("DELETE", "/v1/users/a%20b%2Fc/device_keys/p1", null, null, null, c -> c.users().deleteDeviceKey("a b/c", "p1"));
        expect("POST", "/v1/migrations/a%20b%2Fc/exchange", null, "{\"token\":\"s\"}", "idem-1", c -> c.migrations().exchange("a b/c", new MigrationsExchangeBody().token("s"), "idem-1"));
        expect("GET", "/v1/migrations/a%20b%2Fc/imports/public_key", null, null, null, c -> c.migrations().imports().getPublicKey("a b/c"));
        expect("POST", "/v1/migrations/a%20b%2Fc/imports/public_key/rotate", null, null, "idem-1", c -> c.migrations().imports().rotatePublicKey("a b/c", "idem-1"));
        expect("POST", "/v1/migrations/a%20b%2Fc/imports", null, "{}", "idem-1", c -> c.migrations().imports().create("a b/c", new MigrationsImportsCreateBody(), "idem-1"));
        expect("GET", "/v1/migrations/a%20b%2Fc/imports", "limit=1", null, null, c -> c.migrations().imports().list("a b/c", 1L, null));
        expect("GET", "/v1/migrations/a%20b%2Fc/imports/p1", null, null, null, c -> c.migrations().imports().get("a b/c", "p1"));
        expect("DELETE", "/v1/migrations/a%20b%2Fc/imports/p1", null, null, null, c -> c.migrations().imports().delete("a b/c", "p1"));
        expect("PUT", "/v1/migrations/a%20b%2Fc/imports/p1/parts/p2", null, "{\"data\":\"s\"}", null, c -> c.migrations().imports().putPart("a b/c", "p1", "p2", new MigrationsImportsPutPartBody().data("s")));
        expect("POST", "/v1/migrations/a%20b%2Fc/imports/p1/finalize", null, null, "idem-1", c -> c.migrations().imports().finalizeValue("a b/c", "p1", "idem-1"));
        expect("POST", "/v1/migrations/a%20b%2Fc/imports/p1/dry_run", null, "{}", "idem-1", c -> c.migrations().imports().dryRun("a b/c", "p1", new MigrationsImportsCreateBody(), "idem-1"));
        expect("POST", "/v1/migrations/a%20b%2Fc/imports/p1/apply", null, "{\"confirm_conflicts\":true}", "idem-1", c -> c.migrations().imports().apply("a b/c", "p1", new MigrationsImportsApplyBody().confirmConflicts(true), "idem-1"));
        expect("GET", "/v1/grant_management/grants", "user_id=s", null, null, c -> c.managedGrants().list("s", null, null, null, null));
        expect("GET", "/v1/grant_management/grants/a%20b%2Fc", null, null, null, c -> c.managedGrants().get("a b/c"));
        expect("PATCH", "/v1/grant_management/grants/a%20b%2Fc", null, "{}", null, c -> c.managedGrants().narrow("a b/c", new ManagedGrantsNarrowBody()));
        expect("DELETE", "/v1/grant_management/grants/a%20b%2Fc", null, null, null, c -> c.managedGrants().revoke("a b/c"));
        expect("GET", "/v1/least_privilege/report", "days=1", null, null, c -> c.leastPrivilege().report(1L, null, null, null, null, null, null));
    }

    @Test
    void operations03() throws IOException {
        expect("GET", "/v1/least_privilege/report/a%20b%2Fc/p1", "days=1", null, null, c -> c.leastPrivilege().get("a b/c", "p1", 1L));
        expect("POST", "/v1/least_privilege/apply", null, "{\"subject_type\":\"s\"}", "idem-1", c -> c.leastPrivilege().apply(new LeastPrivilegeApplyBody().subjectType("s"), "idem-1"));
        expect("GET", "/v1/delivery_health/a%20b%2Fc/p1", null, null, null, c -> c.deliveryHealth().get("a b/c", "p1"));
        expect("PUT", "/v1/organizations/a%20b%2Fc/entitlements", null, "{\"plan\":\"s\"}", null, c -> c.flags().assignOrganizationEntitlements("a b/c", new FlagsAssignOrganizationEntitlementsBody().plan("s")));
        expect("PUT", "/v1/instance/yubico_secret", null, "{\"secret\":\"s\"}", null, c -> c.mfaFactors().setYubicoSecret(new MfaFactorsSetYubicoSecretBody().secret("s")));
        expect("DELETE", "/v1/instance/yubico_secret", null, null, null, c -> c.mfaFactors().removeYubicoSecret());
        expect("PUT", "/v1/instance/duo_secret", null, "{\"secret\":\"s\"}", null, c -> c.mfaFactors().setDuoSecret(new MfaFactorsSetDuoSecretBody().secret("s")));
        expect("DELETE", "/v1/instance/duo_secret", null, null, null, c -> c.mfaFactors().removeDuoSecret());
        expect("PUT", "/v1/instance/smart_card_secret", null, "{\"secret\":\"s\"}", null, c -> c.mfaFactors().setSmartCardSecret(new MfaFactorsSetSmartCardSecretBody().secret("s")));
        expect("DELETE", "/v1/instance/smart_card_secret", null, null, null, c -> c.mfaFactors().removeSmartCardSecret());
        expect("POST", "/v1/instance/duo_health_check", null, null, "idem-1", c -> c.mfaFactors().duoHealthCheck("idem-1"));
        expect("GET", "/v1/yubikeys", "limit=1", null, null, c -> c.mfaFactors().yubikeys().list(1L, null));
        expect("POST", "/v1/yubikeys", null, "{\"public_id\":\"s\"}", "idem-1", c -> c.mfaFactors().yubikeys().register(new MfaFactorsYubikeysRegisterBody().publicId("s"), "idem-1"));
        expect("DELETE", "/v1/yubikeys/a%20b%2Fc", null, null, null, c -> c.mfaFactors().yubikeys().remove("a b/c"));
        expect("GET", "/v1/users/a%20b%2Fc/smartcard_mappings", "limit=1", null, null, c -> c.mfaFactors().smartCardMappings().list("a b/c", 1L, null));
        expect("POST", "/v1/users/a%20b%2Fc/smartcard_mappings", null, "{\"kind\":\"s\"}", "idem-1", c -> c.mfaFactors().smartCardMappings().add("a b/c", new MfaFactorsSmartCardMappingsAddBody().kind("s"), "idem-1"));
        expect("DELETE", "/v1/users/a%20b%2Fc/smartcard_mappings/p1", null, null, null, c -> c.mfaFactors().smartCardMappings().remove("a b/c", "p1"));
        expect("DELETE", "/v1/users/a%20b%2Fc/security_questions", null, null, null, c -> c.mfaFactors().resetSecurityQuestions("a b/c"));
        expect("GET", "/v1/analytics/funnel", "kind=s", null, null, c -> c.analytics().funnel("s", null, null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/journey_funnel", "journey_id=s&from=s", null, null, c -> c.analytics().journeyFunnel("s", "s", null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/failures", "from=s", null, null, c -> c.analytics().failures("s", null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/breakdown", "dimension=s", null, null, c -> c.analytics().breakdown("s", null, null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/active_users", "from=s", null, null, c -> c.analytics().activeUsers("s", null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/passkey_adoption", "from=s", null, null, c -> c.analytics().passkeyAdoption("s", null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/reports/inactive_users", "inactive_days=1", null, null, c -> c.analytics().reports().inactiveUsers(1L, null, null, null, null, null, null, null, null));
    }

    @Test
    void operations04() throws IOException {
        expect("GET", "/v1/analytics/reports/mfa_coverage", "from=s", null, null, c -> c.analytics().reports().mfaCoverage("s", null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/reports/access_by_app", "from=s", null, null, c -> c.analytics().reports().accessByApp("s", null, null, null, null, null, null, null));
        expect("GET", "/v1/analytics/api_usage", "subject_type=s", null, null, c -> c.analytics().apiUsage("s", null, null, null, null));
        expect("GET", "/v1/analytics/alert_rules", "limit=1", null, null, c -> c.analytics().alertRules().list(1L, null));
        expect("POST", "/v1/analytics/alert_rules", null, "{\"name\":\"s\"}", "idem-1", c -> c.analytics().alertRules().create(new AnalyticsAlertRulesCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/analytics/alert_rules/a%20b%2Fc", null, null, null, c -> c.analytics().alertRules().get("a b/c"));
        expect("PATCH", "/v1/analytics/alert_rules/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.analytics().alertRules().update("a b/c", new AnalyticsAlertRulesUpdateBody().name("s")));
        expect("DELETE", "/v1/analytics/alert_rules/a%20b%2Fc", null, null, null, c -> c.analytics().alertRules().delete("a b/c"));
        expect("POST", "/v1/analytics/alert_rules/a%20b%2Fc/test", null, "{\"notify\":true}", "idem-1", c -> c.analytics().alertRules().test("a b/c", new AnalyticsAlertRulesTestBody().notifyValue(true), "idem-1"));
        expect("GET", "/v1/marketing_settings", null, null, null, c -> c.marketing().getSettings());
        expect("PUT", "/v1/marketing_settings", null, "{}", null, c -> c.marketing().updateSettings(new MarketingUpdateSettingsBody()));
        expect("GET", "/v1/leads", "limit=1", null, null, c -> c.marketing().listLeads(1L, null, null, null));
        expect("DELETE", "/v1/leads/a%20b%2Fc", null, null, null, c -> c.marketing().deleteLead("a b/c"));
        expect("GET", "/v1/audit_event_schemas", "limit=1", null, null, c -> c.auditEventSchemas().list(1L, null));
        expect("POST", "/v1/audit_event_schemas", null, "{\"action\":\"s\"}", "idem-1", c -> c.auditEventSchemas().register(new AuditEventSchemasRegisterBody().action("s"), "idem-1"));
        expect("GET", "/v1/audit_event_schemas/a%20b%2Fc", null, null, null, c -> c.auditEventSchemas().get("a b/c"));
        expect("PATCH", "/v1/audit_event_schemas/a%20b%2Fc", null, "{\"description\":\"s\"}", null, c -> c.auditEventSchemas().update("a b/c", new AuditEventSchemasUpdateBody().description("s")));
        expect("DELETE", "/v1/audit_event_schemas/a%20b%2Fc", null, null, null, c -> c.auditEventSchemas().delete("a b/c"));
        expect("POST", "/v1/audit_events/a%20b%2Fc/redact", null, "{\"reason\":\"s\"}", "idem-1", c -> c.auditEventSchemas().redact("a b/c", new AuditEventSchemasRedactBody().reason("s"), "idem-1"));
        expect("GET", "/v1/audit_events/a%20b%2Fc/redactions", "limit=1", null, null, c -> c.auditEventSchemas().redactions("a b/c", 1L, null));
        expect("GET", "/v1/migrations/sources", "limit=1", null, null, c -> c.migrations().sources(1L, null));
        expect("GET", "/v1/migrations/a%20b%2Fc/bridge", null, null, null, c -> c.migrations().getBridge("a b/c"));
        expect("PUT", "/v1/migrations/a%20b%2Fc/bridge", null, "{\"enabled\":true}", null, c -> c.migrations().setBridge("a b/c", new MigrationsSetBridgeBody().enabled(true)));
        expect("DELETE", "/v1/migrations/a%20b%2Fc/bridge", null, null, null, c -> c.migrations().deleteBridge("a b/c"));
        expect("GET", "/v1/directory_pull_sources", "organization_id=s", null, null, c -> c.directoryPullSources().list("s", null, null));
    }

    @Test
    void operations05() throws IOException {
        expect("POST", "/v1/directory_pull_sources", null, "{\"organization_id\":\"s\"}", "idem-1", c -> c.directoryPullSources().create(new DirectoryPullSourcesCreateBody().organizationId("s"), "idem-1"));
        expect("GET", "/v1/directory_pull_sources/a%20b%2Fc", null, null, null, c -> c.directoryPullSources().get("a b/c"));
        expect("PATCH", "/v1/directory_pull_sources/a%20b%2Fc", null, "{\"organization_id\":\"s\"}", null, c -> c.directoryPullSources().update("a b/c", new DirectoryPullSourcesUpdateBody().organizationId("s")));
        expect("DELETE", "/v1/directory_pull_sources/a%20b%2Fc", null, null, null, c -> c.directoryPullSources().delete("a b/c"));
        expect("PUT", "/v1/directory_pull_sources/a%20b%2Fc/csv", null, "{\"csv\":\"s\"}", null, c -> c.directoryPullSources().setCsv("a b/c", new DirectoryPullSourcesSetCsvBody().csv("s")));
        expect("POST", "/v1/directory_pull_sources/a%20b%2Fc/sync_runs", null, null, "idem-1", c -> c.directoryPullSources().syncRuns().create("a b/c", "idem-1"));
        expect("GET", "/v1/directory_pull_sources/a%20b%2Fc/sync_runs", "limit=1", null, null, c -> c.directoryPullSources().syncRuns().list("a b/c", 1L, null));
        expect("GET", "/v1/directory_pull_sources/a%20b%2Fc/sync_runs/p1", null, null, null, c -> c.directoryPullSources().syncRuns().get("a b/c", "p1"));
        expect("GET", "/v1/org_ldap_connections/a%20b%2Fc/sync", null, null, null, c -> c.orgLdapConnections().getSync("a b/c"));
        expect("PUT", "/v1/org_ldap_connections/a%20b%2Fc/sync", null, "{\"enabled\":true}", null, c -> c.orgLdapConnections().setSync("a b/c", new OrgLdapConnectionsSetSyncBody().enabled(true)));
        expect("POST", "/v1/org_ldap_connections/a%20b%2Fc/sync_runs", null, "{\"kind\":\"s\"}", "idem-1", c -> c.orgLdapConnections().syncRuns().create("a b/c", new OrgLdapConnectionsSyncRunsCreateBody().kind("s"), "idem-1"));
        expect("GET", "/v1/org_ldap_connections/a%20b%2Fc/sync_runs", "limit=1", null, null, c -> c.orgLdapConnections().syncRuns().list("a b/c", 1L, null));
        expect("GET", "/v1/org_ldap_connections/a%20b%2Fc/sync_runs/p1", null, null, null, c -> c.orgLdapConnections().syncRuns().get("a b/c", "p1"));
        expect("GET", "/v1/relay_agents", "limit=1", null, null, c -> c.relayAgents().list(1L, null));
        expect("POST", "/v1/relay_agents", null, "{\"name\":\"s\"}", "idem-1", c -> c.relayAgents().create(new RelayAgentsCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/relay_agents/a%20b%2Fc", null, null, null, c -> c.relayAgents().get("a b/c"));
        expect("PATCH", "/v1/relay_agents/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.relayAgents().update("a b/c", new RelayAgentsUpdateBody().name("s")));
        expect("DELETE", "/v1/relay_agents/a%20b%2Fc", null, null, null, c -> c.relayAgents().delete("a b/c"));
        expect("POST", "/v1/relay_agents/a%20b%2Fc/rotate_secret", null, null, "idem-1", c -> c.relayAgents().rotateSecret("a b/c", "idem-1"));
        expect("GET", "/v1/org_ldap_connections/a%20b%2Fc/relay", null, null, null, c -> c.orgLdapConnections().getRelay("a b/c"));
        expect("PUT", "/v1/org_ldap_connections/a%20b%2Fc/relay", null, "{\"agent_id\":\"s\"}", null, c -> c.orgLdapConnections().setRelay("a b/c", new OrgLdapConnectionsSetRelayBody().agentId("s")));
        expect("GET", "/v1/oauth_clients/a%20b%2Fc/app_settings", null, null, null, c -> c.oauthClients().appSettings().get("a b/c"));
        expect("PATCH", "/v1/oauth_clients/a%20b%2Fc/app_settings", null, "{}", null, c -> c.oauthClients().appSettings().update("a b/c", new OAuthClientsAppSettingsUpdateBody()));
        expect("GET", "/v1/workload_identity/presets", "limit=1", null, null, c -> c.oauthClients().workloadIdentity().presets(1L, null));
        expect("GET", "/v1/workload_identity/cloud_trust", "provider=s&audience=s&subject=s&tenant_id=s", null, null, c -> c.oauthClients().workloadIdentity().cloudTrust("s", "s", "s", "s"));
    }

    @Test
    void operations06() throws IOException {
        expect("GET", "/v1/oauth_clients/a%20b%2Fc/trusted_issuers", "limit=1", null, null, c -> c.oauthClients().trustedIssuers().list("a b/c", 1L, null));
        expect("POST", "/v1/oauth_clients/a%20b%2Fc/trusted_issuers", null, "{\"issuer\":\"s\"}", "idem-1", c -> c.oauthClients().trustedIssuers().create("a b/c", new OAuthClientsTrustedIssuersCreateBody().issuer("s"), "idem-1"));
        expect("DELETE", "/v1/oauth_clients/a%20b%2Fc/trusted_issuers/p1", null, null, null, c -> c.oauthClients().trustedIssuers().delete("a b/c", "p1"));
        expect("GET", "/v1/oauth_clients/a%20b%2Fc/exchange_policy", null, null, null, c -> c.oauthClients().getExchangePolicy("a b/c"));
        expect("PUT", "/v1/oauth_clients/a%20b%2Fc/exchange_policy", null, "{\"allow_impersonation\":true}", null, c -> c.oauthClients().setExchangePolicy("a b/c", new OAuthClientsSetExchangePolicyBody().allowImpersonation(true)));
        expect("DELETE", "/v1/oauth_clients/a%20b%2Fc/exchange_policy", null, null, null, c -> c.oauthClients().deleteExchangePolicy("a b/c"));
        expect("GET", "/v1/organizations/a%20b%2Fc/connected_apps", "limit=1", null, null, c -> c.organizations().listConnectedApps("a b/c", 1L, null));
        expect("DELETE", "/v1/organizations/a%20b%2Fc/connected_apps/p1", null, null, null, c -> c.organizations().revokeConnectedApp("a b/c", "p1"));
        expect("GET", "/v1/oauth_client_profiles", "limit=1", null, null, c -> c.oauthClientProfiles().list(1L, null));
        expect("PUT", "/v1/oauth_client_profiles/a%20b%2Fc", null, "{\"description\":\"s\"}", null, c -> c.oauthClientProfiles().set("a b/c", new OauthClientProfilesSetBody().description("s")));
        expect("DELETE", "/v1/oauth_client_profiles/a%20b%2Fc", null, null, null, c -> c.oauthClientProfiles().delete("a b/c"));
        expect("PUT", "/v1/oauth_clients/a%20b%2Fc/profile", null, "{\"profile\":\"s\"}", null, c -> c.oauthClients().setProfile("a b/c", new OAuthClientsSetProfileBody().profile("s")));
        expect("PUT", "/v1/oauth_clients/a%20b%2Fc/delegated_ui", null, "{\"login_url\":\"s\"}", null, c -> c.oauthClients().setDelegatedUi("a b/c", new OAuthClientsSetDelegatedUiBody().loginUrl("s")));
        expect("GET", "/v1/oauth_login_requests", "login_challenge=s", null, null, c -> c.oauthClients().loginRequests().get("s"));
        expect("PUT", "/v1/oauth_login_requests/accept", "login_challenge=s", "{\"subject\":\"s\"}", null, c -> c.oauthClients().loginRequests().accept("s", new OAuthClientsLoginRequestsAcceptBody().subject("s")));
        expect("PUT", "/v1/oauth_login_requests/reject", "login_challenge=s", "{\"error\":\"s\"}", null, c -> c.oauthClients().loginRequests().reject("s", new OAuthClientsLoginRequestsRejectBody().error("s")));
        expect("GET", "/v1/oauth_consent_requests", "consent_challenge=s", null, null, c -> c.oauthClients().consentRequests().get("s"));
        expect("PUT", "/v1/oauth_consent_requests/accept", "consent_challenge=s", "{\"remember\":true}", null, c -> c.oauthClients().consentRequests().accept("s", new OAuthClientsConsentRequestsAcceptBody().remember(true)));
        expect("PUT", "/v1/oauth_consent_requests/reject", "consent_challenge=s", "{\"error\":\"s\"}", null, c -> c.oauthClients().consentRequests().reject("s", new OAuthClientsLoginRequestsRejectBody().error("s")));
        expect("POST", "/v1/passkey_api/registration/init", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.passkeyApi().registrationInit(new PasskeyApiRegistrationInitBody().userId("s"), "idem-1"));
        expect("POST", "/v1/passkey_api/registration/finalize", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.passkeyApi().registrationFinalize(new PasskeyApiRegistrationFinalizeBody().userId("s"), "idem-1"));
        expect("POST", "/v1/passkey_api/login/init", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.passkeyApi().loginInit(new PasskeyApiLoginInitBody().userId("s"), "idem-1"));
        expect("POST", "/v1/passkey_api/login/finalize", null, "{\"handle\":\"s\"}", "idem-1", c -> c.passkeyApi().loginFinalize(new PasskeyApiLoginFinalizeBody().handle("s"), "idem-1"));
        expect("GET", "/v1/passkey_api/users/a%20b%2Fc/passkeys", "limit=1", null, null, c -> c.passkeyApi().list("a b/c", 1L, null));
        expect("DELETE", "/v1/passkey_api/users/a%20b%2Fc/passkeys/p1", null, null, null, c -> c.passkeyApi().delete("a b/c", "p1"));
    }

    @Test
    void operations07() throws IOException {
        expect("GET", "/v1/passkey_api/usage", null, null, null, c -> c.passkeyApi().usage());
        expect("GET", "/v1/vc/credential_types", "limit=1", null, null, c -> c.vc().credentialTypes().list(1L, null));
        expect("POST", "/v1/vc/credential_types", null, "{\"vct\":\"s\"}", "idem-1", c -> c.vc().credentialTypes().create(new VcCredentialTypesCreateBody().vct("s"), "idem-1"));
        expect("GET", "/v1/vc/credential_types/a%20b%2Fc", null, null, null, c -> c.vc().credentialTypes().get("a b/c"));
        expect("DELETE", "/v1/vc/credential_types/a%20b%2Fc", null, null, null, c -> c.vc().credentialTypes().delete("a b/c"));
        expect("POST", "/v1/vc/offers", null, "{\"credential_type_id\":\"s\"}", "idem-1", c -> c.vc().createOffer(new VcCreateOfferBody().credentialTypeId("s"), "idem-1"));
        expect("GET", "/v1/vc/credentials", "user_id=s", null, null, c -> c.vc().credentials().list("s", null, null));
        expect("POST", "/v1/vc/credentials/a%20b%2Fc/revoke", null, "{\"k\":\"v\"}", "idem-1", c -> c.vc().credentials().revoke("a b/c", Map.<String, Object>of("k", "v"), "idem-1"));
        expect("POST", "/v1/vc/presentation_requests", null, "{\"vct\":\"s\"}", "idem-1", c -> c.vc().presentationRequests().create(new VcPresentationRequestsCreateBody().vct("s"), "idem-1"));
        expect("GET", "/v1/vc/presentation_requests/a%20b%2Fc", null, null, null, c -> c.vc().presentationRequests().get("a b/c"));
        expect("POST", "/v1/listen/sessions", null, "{\"ttl_seconds\":1}", "idem-1", c -> c.listen().createSession(new ListenCreateSessionBody().ttlSeconds(1L), "idem-1"));
        expect("GET", "/v1/listen/sessions/a%20b%2Fc/stream", "events=s", null, null, c -> c.listen().streamSession("a b/c", "s", null, null, null, null));
        expect("GET", "/v1/agent_registration", null, null, null, c -> c.agentRegistration().get());
        expect("PUT", "/v1/agent_registration", null, "{\"enabled\":true}", null, c -> c.agentRegistration().replace(new AgentRegistrationReplaceBody().enabled(true)));
        expect("POST", "/v1/agents/a%20b%2Fc/claim", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.agents().claim("a b/c", new AgentsClaimBody().userId("s"), "idem-1"));
        expect("POST", "/v1/agent_access_requests/a%20b%2Fc/approve", null, "{\"expires_in_seconds\":1}", "idem-1", c -> c.agentAccessRequests().approve("a b/c", new AgentAccessRequestsApproveBody().expiresInSeconds(1L), "idem-1"));
        expect("POST", "/v1/agent_access_requests/a%20b%2Fc/deny", null, "{\"decided_by\":\"s\"}", "idem-1", c -> c.agentAccessRequests().deny("a b/c", new AgentAccessRequestsDenyBody().decidedBy("s"), "idem-1"));
        expect("GET", "/v1/agent_access_requests", "status=s", null, null, c -> c.agentAccessRequests().list("s", null, null, null));
        expect("GET", "/v1/agents", "owner_user_id=s", null, null, c -> c.agents().list("s", null, null, null));
        expect("GET", "/v1/agents/a%20b%2Fc", null, null, null, c -> c.agents().get("a b/c"));
        expect("PUT", "/v1/agents/a%20b%2Fc/owner", null, "{\"user_id\":\"s\"}", null, c -> c.agents().setOwner("a b/c", new AgentsSetOwnerBody().userId("s")));
        expect("POST", "/v1/agents/a%20b%2Fc/revoke_access", null, "{\"disable\":true}", "idem-1", c -> c.agents().revokeAccess("a b/c", new AgentsRevokeAccessBody().disable(true), "idem-1"));
        expect("GET", "/v1/actions/a%20b%2Fc/calls", "limit=1", null, null, c -> c.actions().calls("a b/c", 1L, null, null, null));
        expect("GET", "/v1/workflows", "limit=1", null, null, c -> c.workflows().list(1L, null));
        expect("POST", "/v1/workflows", null, "{\"name\":\"s\"}", "idem-1", c -> c.workflows().create(new WorkflowsCreateBody().name("s"), "idem-1"));
    }

    @Test
    void operations08() throws IOException {
        expect("GET", "/v1/workflows/a%20b%2Fc", null, null, null, c -> c.workflows().get("a b/c"));
        expect("PATCH", "/v1/workflows/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.workflows().update("a b/c", new WorkflowsCreateBody().name("s")));
        expect("DELETE", "/v1/workflows/a%20b%2Fc", null, null, null, c -> c.workflows().delete("a b/c"));
        expect("POST", "/v1/workflows/a%20b%2Fc/dry_run", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.workflows().dryRun("a b/c", new WorkflowsDryRunBody().userId("s"), "idem-1"));
        expect("POST", "/v1/workflows/a%20b%2Fc/run", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.workflows().run("a b/c", new WorkflowsDryRunBody().userId("s"), "idem-1"));
        expect("GET", "/v1/workflows/a%20b%2Fc/runs", "limit=1", null, null, c -> c.workflows().runs("a b/c", 1L, null));
        expect("GET", "/v1/instance/page_templates", "limit=1", null, null, c -> c.pageTemplates().list(1L, null));
        expect("GET", "/v1/instance/page_templates/a%20b%2Fc", null, null, null, c -> c.pageTemplates().get("a b/c"));
        expect("PUT", "/v1/instance/page_templates/a%20b%2Fc", null, "{}", null, c -> c.pageTemplates().saveDraft("a b/c", new PageTemplatesSaveDraftBody()));
        expect("DELETE", "/v1/instance/page_templates/a%20b%2Fc", "status=s", null, null, c -> c.pageTemplates().discard("a b/c", "s"));
        expect("POST", "/v1/instance/page_templates/a%20b%2Fc/preview", null, null, "idem-1", c -> c.pageTemplates().preview("a b/c", "idem-1"));
        expect("POST", "/v1/instance/page_templates/a%20b%2Fc/promote", null, null, "idem-1", c -> c.pageTemplates().promote("a b/c", "idem-1"));
        expect("GET", "/v1/service_connector_catalog", "limit=1", null, null, c -> c.serviceConnectors().catalog(1L, null));
        expect("GET", "/v1/service_connectors", "limit=1", null, null, c -> c.serviceConnectors().list(1L, null));
        expect("POST", "/v1/service_connectors", null, "{\"name\":\"s\"}", "idem-1", c -> c.serviceConnectors().create(new ServiceConnectorsCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/service_connectors/a%20b%2Fc", null, null, null, c -> c.serviceConnectors().get("a b/c"));
        expect("PATCH", "/v1/service_connectors/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.serviceConnectors().update("a b/c", new ServiceConnectorsUpdateBody().name("s")));
        expect("DELETE", "/v1/service_connectors/a%20b%2Fc", null, null, null, c -> c.serviceConnectors().delete("a b/c"));
        expect("POST", "/v1/service_connectors/a%20b%2Fc/invoke", null, "{\"operation\":\"s\"}", "idem-1", c -> c.serviceConnectors().invoke("a b/c", new ServiceConnectorsInvokeBody().operation("s"), "idem-1"));
        expect("GET", "/v1/desktop_mfa/policy", null, null, null, c -> c.desktopMfa().getPolicy());
        expect("PUT", "/v1/desktop_mfa/policy", null, "{\"enabled\":true}", null, c -> c.desktopMfa().updatePolicy(new DesktopMfaUpdatePolicyBody().enabled(true)));
        expect("POST", "/v1/desktop_mfa/enrolment_codes", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.desktopMfa().createEnrolmentCode(new DesktopMfaCreateEnrolmentCodeBody().userId("s"), "idem-1"));
        expect("GET", "/v1/desktop_mfa/devices", "user_id=s", null, null, c -> c.desktopMfa().listDevices("s", null, null));
        expect("GET", "/v1/desktop_mfa/devices/a%20b%2Fc", null, null, null, c -> c.desktopMfa().getDevice("a b/c"));
        expect("DELETE", "/v1/desktop_mfa/devices/a%20b%2Fc", null, null, null, c -> c.desktopMfa().deleteDevice("a b/c"));
    }

    @Test
    void operations09() throws IOException {
        expect("POST", "/v1/desktop_mfa/devices/a%20b%2Fc/revoke", null, null, "idem-1", c -> c.desktopMfa().revokeDevice("a b/c", "idem-1"));
        expect("POST", "/v1/desktop_mfa/devices/a%20b%2Fc/revoke_offline_cache", null, null, "idem-1", c -> c.desktopMfa().revokeOfflineCache("a b/c", "idem-1"));
        expect("GET", "/v1/identity_verification/settings", null, null, null, c -> c.identityVerification().settings());
        expect("PUT", "/v1/identity_verification/settings", null, "{\"enabled\":true}", null, c -> c.identityVerification().updateSettings(new IdentityVerificationUpdateSettingsBody().enabled(true)));
        expect("GET", "/v1/identity_verifications", "status=s", null, null, c -> c.identityVerification().list("s", null, null));
        expect("GET", "/v1/identity_verifications/a%20b%2Fc", null, null, null, c -> c.identityVerification().get("a b/c"));
        expect("POST", "/v1/identity_verifications/a%20b%2Fc/decide", null, "{\"decision\":\"s\"}", "idem-1", c -> c.identityVerification().decide("a b/c", new IdentityVerificationDecideBody().decision("s"), "idem-1"));
        expect("GET", "/v1/users/a%20b%2Fc/identity_verifications", "limit=1", null, null, c -> c.identityVerification().forUser("a b/c", 1L, null));
        expect("POST", "/v1/users/a%20b%2Fc/identity_verifications", null, "{\"redirect_url\":\"s\"}", "idem-1", c -> c.identityVerification().start("a b/c", new IdentityVerificationStartBody().redirectUrl("s"), "idem-1"));
        expect("GET", "/v1/iga/entitlements", "kind=s", null, null, c -> c.iga().entitlements().list("s", null, null));
        expect("POST", "/v1/iga/entitlements", null, "{\"key\":\"s\"}", "idem-1", c -> c.iga().entitlements().create(new IgaEntitlementsCreateBody().key("s"), "idem-1"));
        expect("GET", "/v1/iga/entitlements/a%20b%2Fc", null, null, null, c -> c.iga().entitlements().get("a b/c"));
        expect("PATCH", "/v1/iga/entitlements/a%20b%2Fc", null, "{\"key\":\"s\"}", null, c -> c.iga().entitlements().update("a b/c", new IgaEntitlementsUpdateBody().key("s")));
        expect("DELETE", "/v1/iga/entitlements/a%20b%2Fc", null, null, null, c -> c.iga().entitlements().delete("a b/c"));
        expect("GET", "/v1/iga/requests", "status=s", null, null, c -> c.iga().requests().list("s", null, null, null, null, null));
        expect("POST", "/v1/iga/requests", null, "{\"entitlement\":\"s\"}", "idem-1", c -> c.iga().requests().create(new IgaRequestsCreateBody().entitlement("s"), "idem-1"));
        expect("GET", "/v1/iga/requests/a%20b%2Fc", null, null, null, c -> c.iga().requests().get("a b/c"));
        expect("POST", "/v1/iga/requests/a%20b%2Fc/decide", null, "{\"decision\":\"s\"}", "idem-1", c -> c.iga().requests().decide("a b/c", new IgaRequestsDecideBody().decision("s"), "idem-1"));
        expect("POST", "/v1/iga/requests/a%20b%2Fc/cancel", null, null, "idem-1", c -> c.iga().requests().cancel("a b/c", "idem-1"));
        expect("GET", "/v1/iga/approvals", "approver_user_id=s&limit=1", null, null, c -> c.iga().requests().inbox("s", 1L, null));
        expect("GET", "/v1/iga/grants", "user_id=s", null, null, c -> c.iga().grants().list("s", null, null, null, null));
        expect("POST", "/v1/iga/grants", null, "{\"entitlement\":\"s\"}", "idem-1", c -> c.iga().grants().create(new IgaGrantsCreateBody().entitlement("s"), "idem-1"));
        expect("POST", "/v1/iga/grants/a%20b%2Fc/revoke", null, "{\"reason\":\"s\"}", "idem-1", c -> c.iga().grants().revoke("a b/c", new IgaGrantsRevokeBody().reason("s"), "idem-1"));
        expect("GET", "/v1/iga/delegations", "delegator_user_id=s", null, null, c -> c.iga().delegations().list("s", null, null));
        expect("POST", "/v1/iga/delegations", null, "{\"delegator_user_id\":\"s\"}", "idem-1", c -> c.iga().delegations().create(new IgaDelegationsCreateBody().delegatorUserId("s"), "idem-1"));
    }

    @Test
    void operations10() throws IOException {
        expect("DELETE", "/v1/iga/delegations/a%20b%2Fc", null, null, null, c -> c.iga().delegations().delete("a b/c"));
        expect("GET", "/v1/iga/sod_rules", "limit=1", null, null, c -> c.iga().sod().listRules(1L, null));
        expect("POST", "/v1/iga/sod_rules", null, "{\"name\":\"s\"}", "idem-1", c -> c.iga().sod().createRule(new IgaSodCreateRuleBody().name("s"), "idem-1"));
        expect("PATCH", "/v1/iga/sod_rules/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.iga().sod().updateRule("a b/c", new IgaSodUpdateRuleBody().name("s")));
        expect("DELETE", "/v1/iga/sod_rules/a%20b%2Fc", null, null, null, c -> c.iga().sod().deleteRule("a b/c"));
        expect("GET", "/v1/iga/sod_violations", "status=s", null, null, c -> c.iga().sod().violations("s", null, null));
        expect("POST", "/v1/iga/sod_violations/scan", null, null, "idem-1", c -> c.iga().sod().scan("idem-1"));
        expect("GET", "/v1/iga/birthright_rules", "limit=1", null, null, c -> c.iga().lifecycle().listBirthrightRules(1L, null));
        expect("POST", "/v1/iga/birthright_rules", null, "{\"name\":\"s\"}", "idem-1", c -> c.iga().lifecycle().createBirthrightRule(new IgaLifecycleCreateBirthrightRuleBody().name("s"), "idem-1"));
        expect("PATCH", "/v1/iga/birthright_rules/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.iga().lifecycle().updateBirthrightRule("a b/c", new IgaLifecycleUpdateBirthrightRuleBody().name("s")));
        expect("DELETE", "/v1/iga/birthright_rules/a%20b%2Fc", null, null, null, c -> c.iga().lifecycle().deleteBirthrightRule("a b/c"));
        expect("POST", "/v1/iga/users/a%20b%2Fc/reconcile", null, null, "idem-1", c -> c.iga().lifecycle().reconcile("a b/c", "idem-1"));
        expect("POST", "/v1/iga/users/a%20b%2Fc/offboard", null, "{\"close_account\":true}", "idem-1", c -> c.iga().lifecycle().offboard("a b/c", new IgaLifecycleOffboardBody().closeAccount(true), "idem-1"));
        expect("GET", "/v1/iga/settings", null, null, null, c -> c.iga().lifecycle().settings());
        expect("PUT", "/v1/iga/settings", null, "{\"jml_enabled\":true}", null, c -> c.iga().lifecycle().updateSettings(new IgaLifecycleUpdateSettingsBody().jmlEnabled(true)));
        expect("GET", "/v1/iga/campaigns", "status=s", null, null, c -> c.iga().campaigns().list("s", null, null));
        expect("POST", "/v1/iga/campaigns", null, "{\"name\":\"s\"}", "idem-1", c -> c.iga().campaigns().create(new IgaCampaignsCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/iga/campaigns/a%20b%2Fc", null, null, null, c -> c.iga().campaigns().get("a b/c"));
        expect("DELETE", "/v1/iga/campaigns/a%20b%2Fc", null, null, null, c -> c.iga().campaigns().delete("a b/c"));
        expect("POST", "/v1/iga/campaigns/a%20b%2Fc/launch", null, null, "idem-1", c -> c.iga().campaigns().launch("a b/c", "idem-1"));
        expect("POST", "/v1/iga/campaigns/a%20b%2Fc/close", null, null, "idem-1", c -> c.iga().campaigns().close("a b/c", "idem-1"));
        expect("GET", "/v1/iga/campaigns/a%20b%2Fc/items", "decision=s", null, null, c -> c.iga().campaigns().items("a b/c", "s", null, null, null));
        expect("GET", "/v1/iga/campaigns/a%20b%2Fc/progress", null, null, null, c -> c.iga().campaigns().progress("a b/c"));
        expect("GET", "/v1/iga/campaigns/a%20b%2Fc/evidence", "format=s", null, null, c -> c.iga().campaigns().evidence("a b/c", "s", null));
        expect("POST", "/v1/iga/campaign_items/a%20b%2Fc/decide", null, "{\"decision\":\"s\"}", "idem-1", c -> c.iga().campaigns().decide("a b/c", new IgaCampaignsDecideBody().decision("s"), "idem-1"));
    }

    @Test
    void operations11() throws IOException {
        expect("GET", "/v1/scim_provisioning_presets", "limit=1", null, null, c -> c.scimProvisioningPresets().list(1L, null));
        expect("GET", "/v1/organizations/a%20b%2Fc/webhook_endpoints", "limit=1", null, null, c -> c.organizations().webhookEndpoints().list("a b/c", 1L, null));
        expect("POST", "/v1/organizations/a%20b%2Fc/webhook_endpoints", null, "{\"url\":\"s\"}", "idem-1", c -> c.organizations().webhookEndpoints().create("a b/c", new OrganizationsWebhookEndpointsCreateBody().url("s"), "idem-1"));
        expect("GET", "/v1/organizations/a%20b%2Fc/webhook_endpoints/p1", null, null, null, c -> c.organizations().webhookEndpoints().get("a b/c", "p1"));
        expect("PATCH", "/v1/organizations/a%20b%2Fc/webhook_endpoints/p1", null, "{\"url\":\"s\"}", null, c -> c.organizations().webhookEndpoints().update("a b/c", "p1", new OrganizationsWebhookEndpointsCreateBody().url("s")));
        expect("DELETE", "/v1/organizations/a%20b%2Fc/webhook_endpoints/p1", null, null, null, c -> c.organizations().webhookEndpoints().delete("a b/c", "p1"));
        expect("POST", "/v1/organizations/a%20b%2Fc/webhook_endpoints/p1/rotate_secret", null, null, "idem-1", c -> c.organizations().webhookEndpoints().rotateSecret("a b/c", "p1", "idem-1"));
        expect("GET", "/v1/organizations/a%20b%2Fc/webhook_endpoints/p1/deliveries", "limit=1", null, null, c -> c.organizations().webhookEndpoints().deliveries("a b/c", "p1", 1L, null, null, null, null, null, null));
        expect("POST", "/v1/vault/objects", null, "{\"name\":\"s\"}", "idem-1", c -> c.vault().create(new VaultCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/vault/objects", "name_prefix=s", null, null, c -> c.vault().list("s", null, null, null));
        expect("GET", "/v1/vault/objects/a%20b%2Fc/metadata", null, null, null, c -> c.vault().metadata("a b/c"));
        expect("GET", "/v1/vault/objects/a%20b%2Fc/versions", "limit=1", null, null, c -> c.vault().versions("a b/c", 1L, null));
        expect("GET", "/v1/vault/objects/a%20b%2Fc", "version=1", null, null, c -> c.vault().read("a b/c", 1L));
        expect("PUT", "/v1/vault/objects/a%20b%2Fc", null, "{\"value\":\"s\"}", null, c -> c.vault().update("a b/c", new VaultUpdateBody().value("s")));
        expect("DELETE", "/v1/vault/objects/a%20b%2Fc", null, null, null, c -> c.vault().delete("a b/c"));
        expect("GET", "/v1/vault/key_provider", null, null, null, c -> c.vault().keyProvider().get());
        expect("PUT", "/v1/vault/key_provider", null, "{\"provider\":\"s\"}", null, c -> c.vault().keyProvider().set(new VaultKeyProviderSetBody().provider("s")));
        expect("DELETE", "/v1/vault/key_provider", null, null, null, c -> c.vault().keyProvider().remove());
        expect("POST", "/v1/connections", null, "{\"provider\":\"s\"}", "idem-1", c -> c.connections().create(new ConnectionsCreateBody().provider("s"), "idem-1"));
        expect("GET", "/v1/connections", "owner_type=s", null, null, c -> c.connections().list("s", null, null, null, null, null, null));
        expect("GET", "/v1/connections/a%20b%2Fc", null, null, null, c -> c.connections().get("a b/c"));
        expect("PATCH", "/v1/connections/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.connections().update("a b/c", new ConnectionsUpdateBody().name("s")));
        expect("DELETE", "/v1/connections/a%20b%2Fc", "revoke=true", null, null, c -> c.connections().delete("a b/c", true));
        expect("PUT", "/v1/connections/a%20b%2Fc/credentials", null, "{\"api_key\":\"s\"}", null, c -> c.connections().setCredentials("a b/c", new ConnectionsSetCredentialsBody().apiKey("s")));
        expect("POST", "/v1/connections/a%20b%2Fc/connect", null, "{\"redirect_url\":\"s\"}", "idem-1", c -> c.connections().connect("a b/c", new ConnectionsConnectBody().redirectUrl("s"), "idem-1"));
    }

    @Test
    void operations12() throws IOException {
        expect("POST", "/v1/connections/a%20b%2Fc/revoke", null, "{\"k\":\"v\"}", "idem-1", c -> c.connections().revoke("a b/c", Map.<String, Object>of("k", "v"), "idem-1"));
        expect("POST", "/v1/connections/a%20b%2Fc/oauth1/sign", null, "{\"method\":\"s\"}", "idem-1", c -> c.connections().oauth1Sign("a b/c", new ConnectionsOauth1SignBody().method("s"), "idem-1"));
        expect("PUT", "/v1/connections/oauth_clients", null, "{\"provider\":\"s\"}", null, c -> c.connections().oauthClients().set(new ConnectionsOauthClientsSetBody().provider("s")));
        expect("GET", "/v1/connections/oauth_clients", "owner_type=s", null, null, c -> c.connections().oauthClients().list("s", null, null, null, null));
        expect("GET", "/v1/connections/oauth_clients/a%20b%2Fc", null, null, null, c -> c.connections().oauthClients().get("a b/c"));
        expect("DELETE", "/v1/connections/oauth_clients/a%20b%2Fc", null, null, null, c -> c.connections().oauthClients().delete("a b/c"));
        expect("POST", "/v1/connections/a%20b%2Fc/relay", null, "{\"method\":\"s\"}", "idem-1", c -> c.connections().relay("a b/c", new ConnectionsRelayBody().method("s"), "idem-1"));
        expect("GET", "/v1/connections/a%20b%2Fc/token", "min_ttl_seconds=1", null, null, c -> c.connections().token("a b/c", 1L, null, null));
        expect("POST", "/v1/connections/a%20b%2Fc/token/refresh", null, "{\"stale_token_hash\":\"s\"}", "idem-1", c -> c.connections().refreshToken("a b/c", new ConnectionsRefreshTokenBody().staleTokenHash("s"), "idem-1"));
        expect("POST", "/v1/connections/tokens/batch", null, "{\"min_ttl_seconds\":1}", "idem-1", c -> c.connections().tokens(new ConnectionsTokensBody().minTtlSeconds(1L), "idem-1"));
        expect("POST", "/v1/connections/import", null, "{\"validate\":true}", "idem-1", c -> c.connections().importConnections(new ConnectionsImportConnectionsBody().validate(true), "idem-1"));
        expect("POST", "/v1/connections/export", null, "{\"owner_id\":\"s\"}", "idem-1", c -> c.connections().exportConnections(new ConnectionsExportConnectionsBody().ownerId("s"), "idem-1"));
        expect("GET", "/v1/connections/a%20b%2Fc/secret", "version=s", null, null, c -> c.connections().secret("a b/c", "s"));
        expect("GET", "/v1/organizations/a%20b%2Fc/connections/p1/secret", "version=s", null, null, c -> c.organizations().getConnectionSecret("a b/c", "p1", "s"));
        expect("GET", "/v1/connections/a%20b%2Fc/credentials/versions", "limit=1", null, null, c -> c.connections().secretVersions("a b/c", 1L, null));
        expect("POST", "/v1/connections/a%20b%2Fc/credentials/rotate", null, "{\"overlap_seconds\":1}", "idem-1", c -> c.connections().rotateCredentials("a b/c", new ConnectionsRotateCredentialsBody().overlapSeconds(1L), "idem-1"));
        expect("POST", "/v1/connections/a%20b%2Fc/webhooks/verify", null, "{\"raw_body\":\"s\"}", "idem-1", c -> c.connections().webhooks().verify("a b/c", new ConnectionsWebhooksVerifyBody().rawBody("s"), "idem-1"));
        expect("POST", "/v1/connections/a%20b%2Fc/webhook_endpoints", null, "{\"forward_url\":\"s\"}", "idem-1", c -> c.connections().webhooks().createEndpoint("a b/c", new ConnectionsWebhooksCreateEndpointBody().forwardUrl("s"), "idem-1"));
        expect("GET", "/v1/connections/a%20b%2Fc/webhook_endpoints", "limit=1", null, null, c -> c.connections().webhooks().listEndpoints("a b/c", 1L, null));
        expect("PATCH", "/v1/connections/a%20b%2Fc/webhook_endpoints/p1", null, "{\"forward_url\":\"s\"}", null, c -> c.connections().webhooks().updateEndpoint("a b/c", "p1", new ConnectionsWebhooksUpdateEndpointBody().forwardUrl("s")));
        expect("DELETE", "/v1/connections/a%20b%2Fc/webhook_endpoints/p1", null, null, null, c -> c.connections().webhooks().deleteEndpoint("a b/c", "p1"));
        expect("POST", "/v1/feature_flags", null, "{\"key\":\"s\"}", "idem-1", c -> c.flags().create(new FlagsCreateBody().key("s"), "idem-1"));
        expect("GET", "/v1/feature_flags", "starting_after=s", null, null, c -> c.flags().list("s", null));
        expect("GET", "/v1/feature_flags/a%20b%2Fc", null, null, null, c -> c.flags().retrieve("a b/c"));
        expect("PATCH", "/v1/feature_flags/a%20b%2Fc", null, "{\"description\":\"s\"}", null, c -> c.flags().update("a b/c", new FlagsUpdateBody().description("s")));
    }

    @Test
    void operations13() throws IOException {
        expect("DELETE", "/v1/feature_flags/a%20b%2Fc", null, null, null, c -> c.flags().delete("a b/c"));
        expect("POST", "/v1/feature_flags/evaluate", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.flags().evaluate(new FlagsEvaluateBody().userId("s"), "idem-1"));
        expect("PUT", "/v1/users/a%20b%2Fc/entitlements", null, "{\"plan\":\"s\"}", null, c -> c.flags().assignUserEntitlements("a b/c", new FlagsAssignOrganizationEntitlementsBody().plan("s")));
        expect("POST", "/v1/fga/stores/a%20b%2Fc/list-users", null, "{\"relation\":\"s\"}", "idem-1", c -> c.fga().listUsers("a b/c", new FgaListUsersBody().relation("s"), "idem-1"));
        expect("GET", "/v1/fga/stores/a%20b%2Fc/changes", "type=s", null, null, c -> c.fga().readChanges("a b/c", "s", null, null, null, null, null));
        expect("POST", "/v1/openfga/stores/a%20b%2Fc/list-users", null, "{\"relation\":\"s\"}", "idem-1", c -> c.openfga().listUsers("a b/c", new OpenFgaListUsersBody().relation("s"), "idem-1"));
        expect("GET", "/v1/openfga/stores/a%20b%2Fc/changes", "type=s", null, null, c -> c.openfga().readChanges("a b/c", "s", null, null, null));
        expect("PUT", "/v1/attack_protection/app_attestation_secret", null, "{\"secret\":\"s\"}", null, c -> c.attackProtection().setAppAttestationSecret(new AttackProtectionSetAppAttestationSecretBody().secret("s")));
        expect("DELETE", "/v1/attack_protection/app_attestation_secret", "kind=s", null, null, c -> c.attackProtection().removeAppAttestationSecret("s"));
        expect("PUT", "/v1/risk_based_mfa/reputation_secret", null, "{\"secret\":\"s\"}", null, c -> c.riskBasedMfa().setReputationSecret(new RiskBasedMfaSetReputationSecretBody().secret("s")));
        expect("DELETE", "/v1/risk_based_mfa/reputation_secret", "provider=s", null, null, c -> c.riskBasedMfa().removeReputationSecret("s"));
        expect("GET", "/v1/ssf/streams", "limit=1", null, null, c -> c.ssf().streams().list(1L, null));
        expect("POST", "/v1/ssf/streams", null, "{\"delivery_method\":\"s\"}", "idem-1", c -> c.ssf().streams().create(new SsfStreamsCreateBody().deliveryMethod("s"), "idem-1"));
        expect("GET", "/v1/ssf/streams/a%20b%2Fc", null, null, null, c -> c.ssf().streams().get("a b/c"));
        expect("PATCH", "/v1/ssf/streams/a%20b%2Fc", null, "{\"endpoint_url\":\"s\"}", null, c -> c.ssf().streams().update("a b/c", new SsfStreamsUpdateBody().endpointUrl("s")));
        expect("DELETE", "/v1/ssf/streams/a%20b%2Fc", null, null, null, c -> c.ssf().streams().delete("a b/c"));
        expect("POST", "/v1/ssf/streams/a%20b%2Fc/verify", null, "{\"state\":\"s\"}", "idem-1", c -> c.ssf().streams().verify("a b/c", new SsfStreamsVerifyBody().state("s"), "idem-1"));
        expect("GET", "/v1/ssf/streams/a%20b%2Fc/status", null, null, null, c -> c.ssf().streams().getStatus("a b/c"));
        expect("POST", "/v1/ssf/streams/a%20b%2Fc/status", null, "{\"status\":\"s\"}", "idem-1", c -> c.ssf().streams().updateStatus("a b/c", new SsfStreamsUpdateStatusBody().status("s"), "idem-1"));
        expect("GET", "/v1/ssf/streams/a%20b%2Fc/events", "limit=1", null, null, c -> c.ssf().streams().events("a b/c", 1L, null));
        expect("GET", "/v1/ssf/receivers", "limit=1", null, null, c -> c.ssf().receivers().list(1L, null));
        expect("POST", "/v1/ssf/receivers", null, "{\"name\":\"s\"}", "idem-1", c -> c.ssf().receivers().create(new SsfReceiversCreateBody().name("s"), "idem-1"));
        expect("PATCH", "/v1/ssf/receivers/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.ssf().receivers().update("a b/c", new SsfReceiversUpdateBody().name("s")));
        expect("DELETE", "/v1/ssf/receivers/a%20b%2Fc", null, null, null, c -> c.ssf().receivers().delete("a b/c"));
        expect("GET", "/v1/network_zones", "limit=1", null, null, c -> c.networkZones().list(1L, null));
    }

    @Test
    void operations14() throws IOException {
        expect("POST", "/v1/network_zones", null, "{\"name\":\"s\"}", "idem-1", c -> c.networkZones().create(new NetworkZonesCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/network_zones/a%20b%2Fc", null, null, null, c -> c.networkZones().get("a b/c"));
        expect("PATCH", "/v1/network_zones/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.networkZones().update("a b/c", new NetworkZonesCreateBody().name("s")));
        expect("DELETE", "/v1/network_zones/a%20b%2Fc", null, null, null, c -> c.networkZones().delete("a b/c"));
        expect("POST", "/v1/network_zones/a%20b%2Fc/check", null, "{\"ip\":\"s\"}", "idem-1", c -> c.networkZones().check("a b/c", new NetworkZonesCheckBody().ip("s"), "idem-1"));
        expect("GET", "/v1/access_policies", "limit=1", null, null, c -> c.accessPolicies().list(1L, null));
        expect("POST", "/v1/access_policies", null, "{\"name\":\"s\"}", "idem-1", c -> c.accessPolicies().create(new AccessPoliciesCreateBody().name("s"), "idem-1"));
        expect("POST", "/v1/access_policies/simulate", null, "{\"policy_id\":\"s\"}", "idem-1", c -> c.accessPolicies().simulate(new AccessPoliciesSimulateBody().policyId("s"), "idem-1"));
        expect("GET", "/v1/access_policies/a%20b%2Fc", null, null, null, c -> c.accessPolicies().get("a b/c"));
        expect("PATCH", "/v1/access_policies/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.accessPolicies().update("a b/c", new AccessPoliciesCreateBody().name("s")));
        expect("DELETE", "/v1/access_policies/a%20b%2Fc", null, null, null, c -> c.accessPolicies().delete("a b/c"));
        expect("GET", "/v1/abac_policies", "limit=1", null, null, c -> c.abac().list(1L, null));
        expect("POST", "/v1/abac_policies", null, "{\"name\":\"s\"}", "idem-1", c -> c.abac().create(new AbacCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/abac_policies/a%20b%2Fc", null, null, null, c -> c.abac().get("a b/c"));
        expect("PATCH", "/v1/abac_policies/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.abac().update("a b/c", new AbacUpdateBody().name("s")));
        expect("DELETE", "/v1/abac_policies/a%20b%2Fc", null, null, null, c -> c.abac().delete("a b/c"));
        expect("GET", "/v1/abac_policies/a%20b%2Fc/versions", "limit=1", null, null, c -> c.abac().versions("a b/c", 1L, null));
        expect("POST", "/v1/abac_policies/a%20b%2Fc/test", null, "{\"version\":1}", "idem-1", c -> c.abac().test("a b/c", new AbacTestBody().version(1L), "idem-1"));
        expect("POST", "/v1/abac/evaluate", null, "{\"action\":\"s\"}", "idem-1", c -> c.abac().evaluate(new AbacEvaluateBody().action("s"), "idem-1"));
        expect("POST", "/v1/abac/partial_evaluate", null, "{\"action\":\"s\"}", "idem-1", c -> c.abac().partialEvaluate(new AbacPartialEvaluateBody().action("s"), "idem-1"));
        expect("POST", "/v1/abac/accessible_resources", null, "{\"action\":\"s\"}", "idem-1", c -> c.abac().accessibleResources(new AbacAccessibleResourcesBody().action("s"), "idem-1"));
        expect("GET", "/v1/abac_templates", "limit=1", null, null, c -> c.abac().templates().list(1L, null));
        expect("POST", "/v1/abac_templates", null, "{\"name\":\"s\"}", "idem-1", c -> c.abac().templates().create(new AbacTemplatesCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/abac_templates/a%20b%2Fc", null, null, null, c -> c.abac().templates().get("a b/c"));
        expect("PATCH", "/v1/abac_templates/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.abac().templates().update("a b/c", new AbacTemplatesUpdateBody().name("s")));
    }

    @Test
    void operations15() throws IOException {
        expect("DELETE", "/v1/abac_templates/a%20b%2Fc", "force=true", null, null, c -> c.abac().templates().delete("a b/c", true));
        expect("GET", "/v1/abac_policy_links", "template_id=s", null, null, c -> c.abac().links().list("s", null, null));
        expect("POST", "/v1/abac_policy_links", null, "{\"template_id\":\"s\"}", "idem-1", c -> c.abac().links().create(new AbacLinksCreateBody().templateId("s"), "idem-1"));
        expect("GET", "/v1/abac_policy_links/a%20b%2Fc", null, null, null, c -> c.abac().links().get("a b/c"));
        expect("PATCH", "/v1/abac_policy_links/a%20b%2Fc", null, "{\"template_id\":\"s\"}", null, c -> c.abac().links().update("a b/c", new AbacLinksUpdateBody().templateId("s")));
        expect("DELETE", "/v1/abac_policy_links/a%20b%2Fc", null, null, null, c -> c.abac().links().delete("a b/c"));
        expect("GET", "/v1/abac_schema", null, null, null, c -> c.abac().schema().get());
        expect("PUT", "/v1/abac_schema", null, "{}", null, c -> c.abac().schema().set(new AbacSchemaSetBody()));
        expect("DELETE", "/v1/abac_schema", null, null, null, c -> c.abac().schema().delete());
        expect("POST", "/v1/abac_schema/validate", null, "{\"source\":\"s\"}", "idem-1", c -> c.abac().schema().validate(new AbacSchemaValidateBody().source("s"), "idem-1"));
        expect("GET", "/v1/abac_policies/a%20b%2Fc/diff", "from=1", null, null, c -> c.abac().diff("a b/c", 1L, null, null, null, null));
        expect("POST", "/v1/abac/diff", null, "{\"before\":\"s\"}", "idem-1", c -> c.abac().diffSources(new AbacDiffSourcesBody().before("s"), "idem-1"));
        expect("GET", "/v1/resource_types", "limit=1", null, null, c -> c.resources().types().list(1L, null));
        expect("POST", "/v1/resource_types", null, "{\"key\":\"s\"}", "idem-1", c -> c.resources().types().create(new ResourcesTypesCreateBody().key("s"), "idem-1"));
        expect("GET", "/v1/resource_types/a%20b%2Fc", null, null, null, c -> c.resources().types().get("a b/c"));
        expect("PATCH", "/v1/resource_types/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.resources().types().update("a b/c", new ResourcesTypesUpdateBody().name("s")));
        expect("DELETE", "/v1/resource_types/a%20b%2Fc", null, null, null, c -> c.resources().types().delete("a b/c"));
        expect("GET", "/v1/resources", "type=s", null, null, c -> c.resources().list("s", null, null, null));
        expect("PUT", "/v1/resources", null, "{\"type\":\"s\"}", null, c -> c.resources().upsert(new ResourcesUpsertBody().type("s")));
        expect("POST", "/v1/resources/sync", null, "{}", "idem-1", c -> c.resources().sync(new ResourcesSyncBody(), "idem-1"));
        expect("GET", "/v1/resources/a%20b%2Fc", null, null, null, c -> c.resources().get("a b/c"));
        expect("DELETE", "/v1/resources/a%20b%2Fc", null, null, null, c -> c.resources().delete("a b/c"));
        expect("GET", "/v1/resource_role_assignments", "resource_id=s", null, null, c -> c.resources().assignments().list("s", null, null, null, null));
        expect("POST", "/v1/resource_role_assignments", null, "{\"role\":\"s\"}", "idem-1", c -> c.resources().assignments().assign(new ResourcesAssignmentsAssignBody().role("s"), "idem-1"));
        expect("DELETE", "/v1/resource_role_assignments/a%20b%2Fc", null, null, null, c -> c.resources().assignments().revoke("a b/c"));
    }

    @Test
    void operations16() throws IOException {
        expect("POST", "/v1/resource_checks", null, "{\"permission\":\"s\"}", "idem-1", c -> c.resources().check(new ResourcesCheckBody().permission("s"), "idem-1"));
        expect("POST", "/v1/resource_checks/batch", null, "{}", "idem-1", c -> c.resources().checkBatch(new ResourcesCheckBatchBody(), "idem-1"));
        expect("POST", "/v1/resource_checks/list", null, "{\"permission\":\"s\"}", "idem-1", c -> c.resources().accessible(new ResourcesAccessibleBody().permission("s"), "idem-1"));
        expect("GET", "/v1/org_role_grants", "grantor_org_id=s", null, null, c -> c.orgRoleGrants().list("s", null, null, null, null));
        expect("POST", "/v1/org_role_grants", null, "{\"grantor_org_id\":\"s\"}", "idem-1", c -> c.orgRoleGrants().create(new OrgRoleGrantsCreateBody().grantorOrgId("s"), "idem-1"));
        expect("GET", "/v1/org_role_grants/a%20b%2Fc", null, null, null, c -> c.orgRoleGrants().get("a b/c"));
        expect("PATCH", "/v1/org_role_grants/a%20b%2Fc", null, "{}", null, c -> c.orgRoleGrants().update("a b/c", new OrgRoleGrantsUpdateBody()));
        expect("DELETE", "/v1/org_role_grants/a%20b%2Fc", null, null, null, c -> c.orgRoleGrants().revoke("a b/c"));
        expect("GET", "/v1/org_role_grants/a%20b%2Fc/delegations", "limit=1", null, null, c -> c.orgRoleGrants().listDelegations("a b/c", 1L, null));
        expect("POST", "/v1/org_role_grants/a%20b%2Fc/delegations", null, "{\"user_id\":\"s\"}", "idem-1", c -> c.orgRoleGrants().delegate("a b/c", new OrgRoleGrantsDelegateBody().userId("s"), "idem-1"));
        expect("DELETE", "/v1/org_role_grants/a%20b%2Fc/delegations/p1", null, null, null, c -> c.orgRoleGrants().undelegate("a b/c", "p1"));
        expect("GET", "/v1/org_role_grants/effective", "user_id=s&organization_id=s", null, null, c -> c.orgRoleGrants().effective("s", "s", null, null));
        expect("GET", "/v1/devices", "user_id=s", null, null, c -> c.devices().list("s", null, null, null));
        expect("PUT", "/v1/devices", null, "{\"external_id\":\"s\"}", null, c -> c.devices().report(new DevicesReportBody().externalId("s")));
        expect("GET", "/v1/devices/a%20b%2Fc", null, null, null, c -> c.devices().get("a b/c"));
        expect("DELETE", "/v1/devices/a%20b%2Fc", null, null, null, c -> c.devices().remove("a b/c"));
        expect("POST", "/v1/device_posture", null, "{}", "idem-1", c -> c.devices().ingestPosture(new DevicesIngestPostureBody(), "idem-1"));
        expect("POST", "/v1/authzen/access/v1/evaluation", null, "{}", "idem-1", c -> c.abac().authzenEvaluation(new AbacAuthzenEvaluationBody(), "idem-1"));
        expect("POST", "/v1/authzen/access/v1/evaluations", null, "{}", "idem-1", c -> c.abac().authzenEvaluations(new AbacAuthzenEvaluationBody(), "idem-1"));
        expect("POST", "/v1/authzen/access/v1/search/subject", null, "{}", "idem-1", c -> c.abac().authzenSearchSubject(new AbacAuthzenSearchSubjectBody(), "idem-1"));
        expect("POST", "/v1/authzen/access/v1/search/resource", null, "{}", "idem-1", c -> c.abac().authzenSearchResource(new AbacAuthzenSearchResourceBody(), "idem-1"));
        expect("POST", "/v1/authzen/access/v1/search/action", null, "{}", "idem-1", c -> c.abac().authzenSearchAction(new AbacAuthzenSearchActionBody(), "idem-1"));
        expect("GET", "/v1/authz_decisions", "source=s", null, null, c -> c.authzDecisions().list("s", null, null, null, null, null, null, null, null, null));
        expect("GET", "/v1/authz_decisions/stats", "since=s", null, null, c -> c.authzDecisions().stats("s", null));
        expect("GET", "/v1/authz_decisions/a%20b%2Fc", null, null, null, c -> c.authzDecisions().get("a b/c"));
    }

    @Test
    void operations17() throws IOException {
        expect("GET", "/v1/authz_decision_settings", null, null, null, c -> c.authzDecisions().settings().get());
        expect("PUT", "/v1/authz_decision_settings", null, "{\"enabled\":true}", null, c -> c.authzDecisions().settings().update(new AuthzDecisionsSettingsUpdateBody().enabled(true)));
        expect("GET", "/v1/policy_bundles/current", null, null, null, c -> c.policyBundles().current());
        expect("GET", "/v1/policy_bundles", "limit=1", null, null, c -> c.policyBundles().list(1L, null));
        expect("POST", "/v1/policy_bundles", null, null, "idem-1", c -> c.policyBundles().export("idem-1"));
        expect("GET", "/v1/policy_bundles/a%20b%2Fc", null, null, null, c -> c.policyBundles().get("a b/c"));
        expect("GET", "/v1/policy_bundles/a%20b%2Fc/archive", null, null, null, c -> c.policyBundles().archive("a b/c"));
        expect("POST", "/v1/policy_bundles/verify", null, "{\"bundle\":\"s\"}", "idem-1", c -> c.policyBundles().verify(new PolicyBundlesVerifyBody().bundle("s"), "idem-1"));
        expect("POST", "/v1/policy_bundles/import", null, "{\"bundle\":\"s\"}", "idem-1", c -> c.policyBundles().importValue(new PolicyBundlesImportBody().bundle("s"), "idem-1"));
        expect("GET", "/v1/proxy_apps", "limit=1", null, null, c -> c.proxyApps().list(1L, null));
        expect("POST", "/v1/proxy_apps", null, "{\"name\":\"s\"}", "idem-1", c -> c.proxyApps().create(new ProxyAppsCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/proxy_apps/a%20b%2Fc", null, null, null, c -> c.proxyApps().get("a b/c"));
        expect("PATCH", "/v1/proxy_apps/a%20b%2Fc", null, "{\"name\":\"s\"}", null, c -> c.proxyApps().update("a b/c", new ProxyAppsUpdateBody().name("s")));
        expect("DELETE", "/v1/proxy_apps/a%20b%2Fc", null, null, null, c -> c.proxyApps().delete("a b/c"));
        expect("GET", "/v1/mcp_gateways", null, null, null, c -> c.mcpGateways().list());
        expect("POST", "/v1/mcp_gateways", null, "{\"name\":\"s\"}", "idem-1", c -> c.mcpGateways().create(new McpGatewaysCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/mcp_gateways/a%20b%2Fc", null, null, null, c -> c.mcpGateways().get("a b/c"));
        expect("PATCH", "/v1/mcp_gateways/a%20b%2Fc", null, "{}", null, c -> c.mcpGateways().update("a b/c", new McpGatewaysUpdateBody()));
        expect("DELETE", "/v1/mcp_gateways/a%20b%2Fc", null, null, null, c -> c.mcpGateways().delete("a b/c"));
        expect("GET", "/v1/mcp_gateways/a%20b%2Fc/budgets", "limit=1", null, null, c -> c.mcpGateways().budgets().list("a b/c", 1L, null));
        expect("POST", "/v1/mcp_gateways/a%20b%2Fc/budgets", null, "{\"name\":\"s\"}", "idem-1", c -> c.mcpGateways().budgets().create("a b/c", new McpGatewaysBudgetsCreateBody().name("s"), "idem-1"));
        expect("GET", "/v1/mcp_gateways/a%20b%2Fc/budgets/p1", null, null, null, c -> c.mcpGateways().budgets().get("a b/c", "p1"));
        expect("PATCH", "/v1/mcp_gateways/a%20b%2Fc/budgets/p1", null, "{\"name\":\"s\"}", null, c -> c.mcpGateways().budgets().update("a b/c", "p1", new McpGatewaysBudgetsCreateBody().name("s")));
        expect("DELETE", "/v1/mcp_gateways/a%20b%2Fc/budgets/p1", null, null, null, c -> c.mcpGateways().budgets().delete("a b/c", "p1"));
        expect("GET", "/v1/mcp_gateways/a%20b%2Fc/budgets/p1/usage", "agent_id=s", null, null, c -> c.mcpGateways().budgets().usage("a b/c", "p1", "s", null));
    }

    @Test
    void operations18() throws IOException {
        expect("POST", "/v1/instance/integrations/a%20b%2Fc/events_secret/rotate", null, "{\"overlap_seconds\":1}", "idem-1", c -> c.instance().integrations().rotateEventsSecret("a b/c", new InstanceIntegrationsRotateEventsSecretBody().overlapSeconds(1L), "idem-1"));
        expect("POST", "/v1/integrations/a%20b%2Fc/card/actions/p1", null, "{\"sub\":\"s\"}", "idem-1", c -> c.integrations().runCardAction("a b/c", "p1", new IntegrationsRunCardActionBody().sub("s"), "idem-1"));
    }

}
