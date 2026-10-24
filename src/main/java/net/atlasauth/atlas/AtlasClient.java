package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.http.Json;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.resources.ActionGallery;
import net.atlasauth.atlas.resources.ActionTemplates;
import net.atlasauth.atlas.resources.Actions;
import net.atlasauth.atlas.resources.CustomOAuthProviders;
import net.atlasauth.atlas.resources.ActorTokens;
import net.atlasauth.atlas.resources.Allowlist;
import net.atlasauth.atlas.resources.AppRoles;
import net.atlasauth.atlas.resources.OAuthInitialAccessTokens;
import net.atlasauth.atlas.resources.Analytics;
import net.atlasauth.atlas.resources.ApiKeys;
import net.atlasauth.atlas.resources.AttackProtection;
import net.atlasauth.atlas.resources.AuditEvents;
import net.atlasauth.atlas.resources.AuditLogs;
import net.atlasauth.atlas.resources.Billing;
import net.atlasauth.atlas.resources.Blocklist;
import net.atlasauth.atlas.resources.BotSignals;
import net.atlasauth.atlas.resources.Branding;
import net.atlasauth.atlas.resources.Captcha;
import net.atlasauth.atlas.resources.ConfigChangelog;
import net.atlasauth.atlas.resources.Connectors;
import net.atlasauth.atlas.resources.DataSubjectRequests;
import net.atlasauth.atlas.resources.Design;
import net.atlasauth.atlas.resources.Diagnostics;
import net.atlasauth.atlas.resources.Domains;
import net.atlasauth.atlas.resources.EmailAddresses;
import net.atlasauth.atlas.resources.EmailTemplates;
import net.atlasauth.atlas.resources.EntitlementOverrides;
import net.atlasauth.atlas.resources.Fga;
import net.atlasauth.atlas.resources.ImportExport;
import net.atlasauth.atlas.resources.Instance;
import net.atlasauth.atlas.resources.InstanceSecurity;
import net.atlasauth.atlas.resources.Invitations;
import net.atlasauth.atlas.resources.JwtTemplates;
import net.atlasauth.atlas.resources.LegacyAuthConnection;
import net.atlasauth.atlas.resources.BetterAuthBridge;
import net.atlasauth.atlas.resources.BetterAuthImports;
import net.atlasauth.atlas.resources.Localizations;
import net.atlasauth.atlas.resources.LogStreams;
import net.atlasauth.atlas.resources.LogoutNotifyEndpoints;
import net.atlasauth.atlas.resources.LtiPlatforms;
import net.atlasauth.atlas.resources.Machines;
import net.atlasauth.atlas.resources.ManagedWaf;
import net.atlasauth.atlas.resources.Mcp;
import net.atlasauth.atlas.resources.McpConnector;
import net.atlasauth.atlas.resources.Messaging;
import net.atlasauth.atlas.resources.NetworkAcls;
import net.atlasauth.atlas.resources.Notifications;
import net.atlasauth.atlas.resources.OAuthClients;
import net.atlasauth.atlas.resources.OAuthProviders;
import net.atlasauth.atlas.resources.OpenFga;
import net.atlasauth.atlas.resources.OrgLdapConnections;
import net.atlasauth.atlas.resources.OrganizationSettingsSchema;
import net.atlasauth.atlas.resources.Organizations;
import net.atlasauth.atlas.resources.Pca;
import net.atlasauth.atlas.resources.Permissions;
import net.atlasauth.atlas.resources.Platform;
import net.atlasauth.atlas.resources.Policies;
import net.atlasauth.atlas.resources.PolicySourceConfig;
import net.atlasauth.atlas.resources.RadiusClients;
import net.atlasauth.atlas.resources.RateLimitPolicy;
import net.atlasauth.atlas.resources.RedirectUrls;
import net.atlasauth.atlas.resources.Referrals;
import net.atlasauth.atlas.resources.ResourceServers;
import net.atlasauth.atlas.resources.RiskBasedMfa;
import net.atlasauth.atlas.resources.Roles;
import net.atlasauth.atlas.resources.ScimProvisioning;
import net.atlasauth.atlas.resources.ScimTokens;
import net.atlasauth.atlas.resources.SessionExchangeApps;
import net.atlasauth.atlas.resources.Sessions;
import net.atlasauth.atlas.resources.SignInTokens;
import net.atlasauth.atlas.resources.SmsTemplates;
import net.atlasauth.atlas.resources.SsoConnections;
import net.atlasauth.atlas.resources.SsoOnboarding;
import net.atlasauth.atlas.resources.Testing;
import net.atlasauth.atlas.resources.Tickets;
import net.atlasauth.atlas.resources.Tokens;
import net.atlasauth.atlas.resources.UserHooks;
import net.atlasauth.atlas.resources.Users;
import net.atlasauth.atlas.resources.Waitlist;
import net.atlasauth.atlas.resources.Webhooks;
import net.atlasauth.atlas.resources.SecretKeys;
import net.atlasauth.atlas.resources.Keys;
import net.atlasauth.atlas.resources.PartnerLinks;
import net.atlasauth.atlas.resources.AtlasIdGrants;
import net.atlasauth.atlas.resources.Privacy;
import net.atlasauth.atlas.resources.Integrations;
import net.atlasauth.atlas.resources.Entitlements;
import net.atlasauth.atlas.resources.Notices;
import net.atlasauth.atlas.resources.SecurityAdvisories;
import net.atlasauth.atlas.resources.SubProcessors;
import net.atlasauth.atlas.resources.AuditExports;
import net.atlasauth.atlas.resources.Status;
import net.atlasauth.atlas.resources.Delivery;

import net.atlasauth.atlas.http.UnknownFieldsWarning;
import net.atlasauth.atlas.models.DryRunResult;
import net.atlasauth.atlas.models.Whoami;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * The typed management client for the Atlas Backend API — the secret-key
 * surface, the Java peer of {@code @clerk/backend}'s {@code createClerkClient}.
 *
 * <p>Each namespace is one bound resource sharing a single {@link Transport}, so
 * this class stays a table of contents: build it once, then reach for
 * {@code client.users()}, {@code client.organizations()}, and the rest.
 *
 * <pre>{@code
 * AtlasClient atlas = AtlasClient.builder()
 *     .secretKey(System.getenv("ATLAS_SECRET_KEY"))
 *     .build();
 *
 * User user = atlas.users().get("usr_123");
 * }</pre>
 *
 * <p>Instances are immutable and thread-safe; create one and share it.
 */
public final class AtlasClient {

    /** The default Backend API origin, overridable per instance via the builder. */
    public static final String DEFAULT_API_URL = "https://api.atlasauth.net";

    private final Transport transport;

    private final Users users;
    private final Sessions sessions;
    private final Organizations organizations;
    private final Roles roles;
    private final Permissions permissions;
    private final OAuthClients oauthClients;
    private final ResourceServers resourceServers;
    private final AppRoles appRoles;
    private final OAuthInitialAccessTokens oauthInitialAccessTokens;
    private final SsoConnections ssoConnections;
    private final ScimTokens scimTokens;
    private final Domains domains;
    private final Waitlist waitlist;
    private final Allowlist allowlist;
    private final Blocklist blocklist;
    private final AttackProtection attackProtection;
    private final ActorTokens actorTokens;
    private final Invitations invitations;
    private final Webhooks webhooks;
    private final SignInTokens signInTokens;
    private final AuditLogs auditLogs;
    private final JwtTemplates jwtTemplates;
    private final ApiKeys apiKeys;
    private final OAuthProviders oauthProviders;
    private final SsoOnboarding ssoOnboarding;
    private final ScimProvisioning scimProvisioning;
    private final Fga fga;
    private final RateLimitPolicy rateLimitPolicy;
    private final RiskBasedMfa riskBasedMfa;
    private final BotSignals botSignals;
    private final NetworkAcls networkAcls;
    private final ManagedWaf managedWaf;
    private final LogStreams logStreams;
    private final LogoutNotifyEndpoints logoutNotifyEndpoints;
    private final Branding branding;
    private final EmailTemplates emailTemplates;
    private final SmsTemplates smsTemplates;
    private final Localizations localizations;
    private final Actions actions;
    private final ActionGallery actionGallery;
    private final ActionTemplates actionTemplates;
    private final CustomOAuthProviders customOAuthProviders;
    private final Billing billing;
    private final Messaging messaging;
    private final ImportExport importExport;
    private final DataSubjectRequests dataSubjectRequests;
    private final RadiusClients radiusClients;
    private final LtiPlatforms ltiPlatforms;
    private final Instance instance;
    private final InstanceSecurity instanceSecurity;
    private final Referrals referrals;
    private final Policies policies;
    private final PolicySourceConfig policySource;
    private final UserHooks userHooks;
    private final Tokens tokens;
    private final Design design;
    private final Platform platform;
    private final EmailAddresses emailAddresses;
    private final Tickets tickets;
    private final OrganizationSettingsSchema organizationSettingsSchema;
    private final Notifications notifications;
    private final Analytics analytics;
    private final LegacyAuthConnection legacyAuthConnection;
    private final BetterAuthImports betterAuthImports;
    private final BetterAuthBridge betterAuthBridge;
    private final Mcp mcp;
    private final Pca pca;
    private final McpConnector mcpConnector;
    private final OrgLdapConnections orgLdapConnections;
    private final SessionExchangeApps sessionExchangeApps;
    private final Diagnostics diagnostics;
    private final ConfigChangelog configChangelog;
    private final RedirectUrls redirectUrls;
    private final Machines machines;
    private final EntitlementOverrides entitlementOverrides;
    private final Connectors connectors;
    private final AuditEvents auditEvents;
    private final Testing testing;
    private final OpenFga openFga;
    private final Captcha captcha;
    private final SecretKeys secretKeys;
    private final Keys keys;
    private final PartnerLinks partnerLinks;
    private final AtlasIdGrants atlasIdGrants;
    private final Privacy privacy;
    private final Integrations integrations;
    private final Entitlements entitlements;
    private final Notices notices;
    private final SecurityAdvisories securityAdvisories;
    private final SubProcessors subProcessors;
    private final AuditExports auditExports;
    private final Status status;
    private final Delivery delivery;

    private final Builder config;

    private AtlasClient(Builder b) {
        boolean hasSecret = b.secretKey != null && !b.secretKey.isEmpty();
        boolean hasPlatform = b.platformKey != null && !b.platformKey.isEmpty();
        if (!hasSecret && !hasPlatform) {
            throw new IllegalArgumentException("AtlasClient requires a secretKey (sk_...) or a platformKey (psk_...).");
        }
        this.config = b.copy();
        String apiUrl = b.apiUrl != null ? b.apiUrl : DEFAULT_API_URL;
        // Build the HttpClient once and keep it on the config, so withStrict() copies share it.
        if (b.httpClient == null) {
            b.httpClient = HttpClient.newHttpClient();
            this.config.httpClient = b.httpClient;
        }
        HttpClient http = b.httpClient;
        ObjectMapper mapper = b.mapper != null ? b.mapper : Json.mapper();
        Duration timeout = b.timeout != null ? b.timeout : Duration.ofSeconds(30);

        this.transport = new Transport(b.secretKey, apiUrl, http, mapper, timeout, b.strict, b.onUnknownFields,
                "This call needs an instance secret key: set AtlasClient.builder().secretKey(\"sk_...\").",
                b.dryRun, b.traceparent, b.secretRecipient);
        Transport platformTransport = new Transport(b.platformKey, apiUrl, http, mapper, timeout, b.strict,
                b.onUnknownFields,
                "Platform calls need a platform key: set AtlasClient.builder().platformKey(\"psk_...\").",
                b.dryRun, b.traceparent, b.secretRecipient);

        this.users = new Users(transport);
        this.sessions = new Sessions(transport);
        this.organizations = new Organizations(transport);
        this.roles = new Roles(transport);
        this.permissions = new Permissions(transport);
        this.oauthClients = new OAuthClients(transport);
        this.resourceServers = new ResourceServers(transport);
        this.appRoles = new AppRoles(transport);
        this.oauthInitialAccessTokens = new OAuthInitialAccessTokens(transport);
        this.ssoConnections = new SsoConnections(transport);
        this.scimTokens = new ScimTokens(transport);
        this.domains = new Domains(transport);
        this.waitlist = new Waitlist(transport);
        this.allowlist = new Allowlist(transport);
        this.blocklist = new Blocklist(transport);
        this.attackProtection = new AttackProtection(transport);
        this.actorTokens = new ActorTokens(transport);
        this.invitations = new Invitations(transport);
        this.webhooks = new Webhooks(transport);
        this.signInTokens = new SignInTokens(transport);
        this.auditLogs = new AuditLogs(transport);
        this.jwtTemplates = new JwtTemplates(transport);
        this.apiKeys = new ApiKeys(transport);
        this.oauthProviders = new OAuthProviders(transport);
        this.ssoOnboarding = new SsoOnboarding(transport);
        this.scimProvisioning = new ScimProvisioning(transport);
        this.fga = new Fga(transport);
        this.rateLimitPolicy = new RateLimitPolicy(transport);
        this.riskBasedMfa = new RiskBasedMfa(transport);
        this.botSignals = new BotSignals(transport);
        this.networkAcls = new NetworkAcls(transport);
        this.managedWaf = new ManagedWaf(transport);
        this.logStreams = new LogStreams(transport);
        this.logoutNotifyEndpoints = new LogoutNotifyEndpoints(transport);
        this.branding = new Branding(transport);
        this.emailTemplates = new EmailTemplates(transport);
        this.smsTemplates = new SmsTemplates(transport);
        this.localizations = new Localizations(transport);
        this.actions = new Actions(transport);
        this.actionGallery = new ActionGallery(transport);
        this.actionTemplates = new ActionTemplates(transport);
        this.customOAuthProviders = new CustomOAuthProviders(transport);
        this.billing = new Billing(transport);
        this.messaging = new Messaging(transport);
        this.importExport = new ImportExport(transport);
        this.dataSubjectRequests = new DataSubjectRequests(transport);
        this.radiusClients = new RadiusClients(transport);
        this.ltiPlatforms = new LtiPlatforms(transport);
        this.instance = new Instance(transport);
        this.instanceSecurity = new InstanceSecurity(transport);
        this.referrals = new Referrals(transport);
        this.policies = new Policies(transport);
        this.policySource = new PolicySourceConfig(transport);
        this.userHooks = new UserHooks(transport);
        this.tokens = new Tokens(transport);
        this.design = new Design(transport);
        this.platform = new Platform(platformTransport);
        this.emailAddresses = new EmailAddresses(transport);
        this.tickets = new Tickets(transport);
        this.organizationSettingsSchema = new OrganizationSettingsSchema(transport);
        this.notifications = new Notifications(transport);
        this.analytics = new Analytics(transport);
        this.legacyAuthConnection = new LegacyAuthConnection(transport);
        this.betterAuthImports = new BetterAuthImports(transport);
        this.betterAuthBridge = new BetterAuthBridge(transport);
        this.mcp = new Mcp(transport);
        this.pca = new Pca(transport);
        this.mcpConnector = new McpConnector(transport);
        this.orgLdapConnections = new OrgLdapConnections(transport);
        this.sessionExchangeApps = new SessionExchangeApps(transport);
        this.diagnostics = new Diagnostics(transport);
        this.configChangelog = new ConfigChangelog(transport);
        this.redirectUrls = new RedirectUrls(transport);
        this.machines = new Machines(transport);
        this.entitlementOverrides = new EntitlementOverrides(transport);
        this.connectors = new Connectors(transport);
        this.auditEvents = new AuditEvents(transport);
        this.testing = new Testing(transport);
        this.openFga = new OpenFga(transport);
        this.captcha = new Captcha(transport);
        this.secretKeys = new SecretKeys(transport);
        this.keys = new Keys(transport);
        this.partnerLinks = new PartnerLinks(transport);
        this.atlasIdGrants = new AtlasIdGrants(transport);
        this.privacy = new Privacy(transport);
        this.integrations = new Integrations(transport);
        this.entitlements = new Entitlements(transport);
        this.notices = new Notices(transport);
        this.securityAdvisories = new SecurityAdvisories(transport);
        this.subProcessors = new SubProcessors(transport);
        this.auditExports = new AuditExports(transport);
        this.status = new Status(transport);
        this.delivery = new Delivery(transport);
    }

    /**
     * A copy of this client that sends {@code Atlas-Strict: <strict>} on every write
     * (a per-call override of the client default). {@code true} turns unknown body
     * fields into a {@code 422 UNKNOWN_FIELD}; {@code false} opts out of an instance
     * whose strict mode is on. The copy shares this client's HTTP connection pool.
     *
     * <pre>{@code
     * atlas.withStrict(true).users().update("usr_123", body);
     * }</pre>
     */
    public AtlasClient withStrict(boolean strict) {
        Builder b = config.copy();
        b.strict = strict;
        return new AtlasClient(b);
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Shortcut for {@code AtlasClient.builder().secretKey(key).build()}. */
    public static AtlasClient of(String secretKey) {
        return builder().secretKey(secretKey).build();
    }

    /** The shared HTTP core, exposed for advanced callers hitting an un-modeled route. */
    public Transport transport() {
        return transport;
    }

    public Users users() {
        return users;
    }

    public Sessions sessions() {
        return sessions;
    }

    public Organizations organizations() {
        return organizations;
    }

    public Roles roles() {
        return roles;
    }

    public Permissions permissions() {
        return permissions;
    }

    public OAuthClients oauthClients() {
        return oauthClients;
    }

    public ResourceServers resourceServers() {
        return resourceServers;
    }

    /** {@code /v1/app_roles} — instance-wide user roles. */
    public AppRoles appRoles() {
        return appRoles;
    }

    /** {@code /v1/oauth_initial_access_tokens} — OAuth dynamic client registration gating. */
    public OAuthInitialAccessTokens oauthInitialAccessTokens() {
        return oauthInitialAccessTokens;
    }

    public SsoConnections ssoConnections() {
        return ssoConnections;
    }

    public ScimTokens scimTokens() {
        return scimTokens;
    }

    public Domains domains() {
        return domains;
    }

    public Waitlist waitlist() {
        return waitlist;
    }

    public Allowlist allowlist() {
        return allowlist;
    }

    public Blocklist blocklist() {
        return blocklist;
    }

    public AttackProtection attackProtection() {
        return attackProtection;
    }

    public ActorTokens actorTokens() {
        return actorTokens;
    }

    public Invitations invitations() {
        return invitations;
    }

    public Webhooks webhooks() {
        return webhooks;
    }

    public SignInTokens signInTokens() {
        return signInTokens;
    }

    public AuditLogs auditLogs() {
        return auditLogs;
    }

    public JwtTemplates jwtTemplates() {
        return jwtTemplates;
    }

    public ApiKeys apiKeys() {
        return apiKeys;
    }

    public OAuthProviders oauthProviders() {
        return oauthProviders;
    }

    public SsoOnboarding ssoOnboarding() {
        return ssoOnboarding;
    }

    public ScimProvisioning scimProvisioning() {
        return scimProvisioning;
    }

    public Fga fga() {
        return fga;
    }

    public RateLimitPolicy rateLimitPolicy() {
        return rateLimitPolicy;
    }

    public RiskBasedMfa riskBasedMfa() {
        return riskBasedMfa;
    }

    public BotSignals botSignals() {
        return botSignals;
    }

    public NetworkAcls networkAcls() {
        return networkAcls;
    }

    public ManagedWaf managedWaf() {
        return managedWaf;
    }

    public LogStreams logStreams() {
        return logStreams;
    }

    /** Logout fan-out endpoints ({@code /v1/logout_notify_endpoints}). */
    public LogoutNotifyEndpoints logoutNotifyEndpoints() {
        return logoutNotifyEndpoints;
    }

    public Branding branding() {
        return branding;
    }

    public EmailTemplates emailTemplates() {
        return emailTemplates;
    }

    public SmsTemplates smsTemplates() {
        return smsTemplates;
    }

    public Localizations localizations() {
        return localizations;
    }

    public Actions actions() {
        return actions;
    }

    /** The community Actions gallery: browse, install and update templates. */
    public ActionGallery actionGallery() {
        return actionGallery;
    }

    /** Action templates this instance publishes to the gallery. */
    public ActionTemplates actionTemplates() {
        return actionTemplates;
    }

    /** Your own upstream OAuth / OIDC login providers. */
    public CustomOAuthProviders customOAuthProviders() {
        return customOAuthProviders;
    }

    public Billing billing() {
        return billing;
    }

    public Messaging messaging() {
        return messaging;
    }

    public ImportExport importExport() {
        return importExport;
    }

    public DataSubjectRequests dataSubjectRequests() {
        return dataSubjectRequests;
    }

    public RadiusClients radiusClients() {
        return radiusClients;
    }

    public LtiPlatforms ltiPlatforms() {
        return ltiPlatforms;
    }

    public Instance instance() {
        return instance;
    }

    public InstanceSecurity instanceSecurity() {
        return instanceSecurity;
    }

    /** Referral attribution: the instance's referral platform connection, connection test and call log. */
    public Referrals referrals() {
        return referrals;
    }

    /** Policy acceptance: policies and versions, acceptances, a user's standing and the {@code pol} claim setting. */
    public Policies policies() {
        return policies;
    }

    /** The external policy source: endpoint, sync mode, write-only secret, test, sync and delivery log. */
    public PolicySourceConfig policySource() {
        return policySource;
    }

    /** The {@code before_user_create} hook: settings and a test call. */
    public UserHooks userHooks() {
        return userHooks;
    }

    public Tokens tokens() {
        return tokens;
    }

    public Design design() {
        return design;
    }

    public Platform platform() {
        return platform;
    }

    public EmailAddresses emailAddresses() {
        return emailAddresses;
    }

    public Tickets tickets() {
        return tickets;
    }

    public OrganizationSettingsSchema organizationSettingsSchema() {
        return organizationSettingsSchema;
    }

    public Notifications notifications() {
        return notifications;
    }

    public Analytics analytics() {
        return analytics;
    }

    public LegacyAuthConnection legacyAuthConnection() {
        return legacyAuthConnection;
    }

    /** Migrate from Better Auth: sealed-bundle imports ({@code /v1/better_auth_imports}). */
    public BetterAuthImports betterAuthImports() {
        return betterAuthImports;
    }

    /** Migrate from Better Auth: the session bridge settings and the secret-key exchange. */
    public BetterAuthBridge betterAuthBridge() {
        return betterAuthBridge;
    }

    public Mcp mcp() {
        return mcp;
    }

    public Pca pca() {
        return pca;
    }

    public McpConnector mcpConnector() {
        return mcpConnector;
    }

    public OrgLdapConnections orgLdapConnections() {
        return orgLdapConnections;
    }

    public SessionExchangeApps sessionExchangeApps() {
        return sessionExchangeApps;
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }

    public ConfigChangelog configChangelog() {
        return configChangelog;
    }

    public RedirectUrls redirectUrls() {
        return redirectUrls;
    }

    public Machines machines() {
        return machines;
    }

    public EntitlementOverrides entitlementOverrides() {
        return entitlementOverrides;
    }

    public Connectors connectors() {
        return connectors;
    }

    public AuditEvents auditEvents() {
        return auditEvents;
    }

    public Testing testing() {
        return testing;
    }

    public OpenFga openFga() {
        return openFga;
    }

    public Captcha captcha() {
        return captcha;
    }


    /** Scoped instance secret keys: mint, list, update, rotate with an overlap, revoke. */
    public SecretKeys secretKeys() {
        return secretKeys;
    }

    /** Every credential on the instance in one shape ({@code /v1/keys}). */
    public Keys keys() {
        return keys;
    }

    /** Credentials integrated partner services hold on this instance, and the ones it holds for partner APIs. */
    public PartnerLinks partnerLinks() {
        return partnerLinks;
    }

    /** Sign in with Atlas grants for the apps this instance manages (privacy requests). */
    public AtlasIdGrants atlasIdGrants() {
        return atlasIdGrants;
    }

    /** The data-rights executor protocol ({@code /v1/privacy}) and the instance's orchestrator connection. */
    public Privacy privacy() {
        return privacy;
    }

    /** The routes an integrated service calls: verify errors, the agent-sidebar card, public instance metadata. */
    public Integrations integrations() {
        return integrations;
    }

    /** Effective entitlements of a user or organization. */
    public Entitlements entitlements() {
        return entitlements;
    }

    /** Operator notices from Atlas. */
    public Notices notices() {
        return notices;
    }

    /** Security advisories. */
    public SecurityAdvisories securityAdvisories() {
        return securityAdvisories;
    }

    /** The third parties that process this instance's data. */
    public SubProcessors subProcessors() {
        return subProcessors;
    }

    /** Stream the audit log to your SIEM as CloudEvents. */
    public AuditExports auditExports() {
        return auditExports;
    }

    /** Atlas service status, the status subscription, incidents and watched dependencies. */
    public Status status() {
        return status;
    }

    /** Outbound delivery health and the egress IPs. */
    public Delivery delivery() {
        return delivery;
    }

    /** {@code GET /v1/whoami} — what the secret key in use is, its scopes and the instance it belongs to. */
    public Whoami whoami() {
        return keys.whoami();
    }

    /**
     * A copy of this client that sends every write (POST, PUT, PATCH, DELETE) with {@code ?dry_run=true} and
     * without an {@code Idempotency-Key}: Atlas validates it and answers with the changes it would make,
     * changing nothing. The typed result of a method called on the copy is mostly empty; use
     * {@link #dryRun(String, String, Object)} to get the plan itself. The copy shares this client's HTTP pool.
     *
     * <pre>{@code
     * atlas.withDryRun().instance().setIntegration("acme", body);
     * }</pre>
     */
    public AtlasClient withDryRun() {
        Builder b = config.copy();
        b.dryRun = true;
        return new AtlasClient(b);
    }

    /**
     * A copy of this client that sends a W3C {@code traceparent} on every call, so Atlas stores the trace id on
     * the webhook deliveries, back-channel deliveries, referral calls and executor requests the call triggers.
     * Mint one with {@link net.atlasauth.atlas.Traceparent#create()}.
     */
    public AtlasClient withTraceparent(String traceparent) {
        Builder b = config.copy();
        b.traceparent = traceparent;
        return new AtlasClient(b);
    }

    /** A copy of this client that sends {@code X-Secret-Recipient} on every call ({@code null} = none). */
    public AtlasClient withSecretRecipient(String secretRecipient) {
        Builder b = config.copy();
        b.secretRecipient = secretRecipient;
        return new AtlasClient(b);
    }

    /**
     * Send one write as a dry run and return the plan. {@code method} is POST, PUT, PATCH or DELETE; {@code body}
     * may be null.
     *
     * <pre>{@code
     * DryRunResult plan = atlas.dryRun("PATCH", "/v1/instance", Map.of("name", "New"));
     * }</pre>
     */
    public DryRunResult dryRun(String method, String path, Object body) {
        return transport.withDryRun(true).request(method.toUpperCase(java.util.Locale.ROOT), path, null, body, null, false,
                new TypeReference<DryRunResult>() {
                });
    }

    /**
     * Fluent builder for {@link AtlasClient}. Set {@code secretKey} for the instance
     * API, {@code platformKey} for {@link #platform()}, or both.
     */
    public static final class Builder {
        private String secretKey;
        private String platformKey;
        private String apiUrl;
        private HttpClient httpClient;
        private ObjectMapper mapper;
        private Duration timeout;
        private Boolean strict;
        private Consumer<UnknownFieldsWarning> onUnknownFields;
        private boolean dryRun;
        private String traceparent;
        private String secretRecipient;

        private Builder copy() {
            Builder b = new Builder();
            b.secretKey = secretKey;
            b.platformKey = platformKey;
            b.apiUrl = apiUrl;
            b.httpClient = httpClient;
            b.mapper = mapper;
            b.timeout = timeout;
            b.strict = strict;
            b.onUnknownFields = onUnknownFields;
            b.dryRun = dryRun;
            b.traceparent = traceparent;
            b.secretRecipient = secretRecipient;
            return b;
        }

        /**
         * The platform key ({@code psk_...}) used by {@link AtlasClient#platform()} to
         * provision apps and register Sign in with Atlas apps. Sent as a Bearer token on
         * platform calls only, never logged.
         */
        public Builder platformKey(String platformKey) {
            this.platformKey = platformKey;
            return this;
        }

        /**
         * Strict request bodies: {@code true} sends {@code Atlas-Strict: true} on every
         * write, so a body field the endpoint does not accept is a {@code 422
         * UNKNOWN_FIELD} (naming the field) before anything changes; {@code false} sends
         * {@code Atlas-Strict: false} to opt out of an instance that defaults to strict.
         * Unset sends no header. Override per call with {@link AtlasClient#withStrict(boolean)}.
         */
        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        /**
         * Called when a successful write ignored body fields the endpoint does not
         * accept (the {@code Atlas-Unknown-Fields} response header). Defaults to a
         * {@code WARNING} on the {@code net.atlasauth.atlas} {@link System.Logger}.
         */
        public Builder onUnknownFields(Consumer<UnknownFieldsWarning> onUnknownFields) {
            this.onUnknownFields = onUnknownFields;
            return this;
        }

        /**
         * Send {@code X-Secret-Recipient: <recipient>} (an {@code x25519:<base64 public key>}) on every call: each shown-once
         * secret in an answer comes back sealed to it ({@code sealed_secret}, {@code sealed_client_secret},
         * {@code sealed_webhook_secret}, with {@code secrets_sealed} naming the fields) and the plaintext field is absent.
         */
        public Builder secretRecipient(String secretRecipient) {
            this.secretRecipient = secretRecipient;
            return this;
        }

        /** Send a W3C {@code traceparent} on every call (see {@link AtlasClient#withTraceparent(String)}). */
        public Builder traceparent(String traceparent) {
            this.traceparent = traceparent;
            return this;
        }

        /** The instance secret key ({@code sk_...}, or the suite format {@code atlas_<env>_sk_...}). Sent as a Bearer token, never logged. */
        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        /** Base URL of the instance's Backend API. Defaults to {@link #DEFAULT_API_URL}. */
        public Builder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        /** Inject a shared/tuned {@link HttpClient} (proxy, connection pool, timeouts). */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /** Override the JSON mapper (rarely needed; the default matches the wire). */
        public Builder objectMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        /** Per-request timeout. Defaults to 30 seconds. */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public AtlasClient build() {
            return new AtlasClient(this);
        }
    }
}
