package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.http.Json;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.resources.Actions;
import net.atlasauth.atlas.resources.ActorTokens;
import net.atlasauth.atlas.resources.Allowlist;
import net.atlasauth.atlas.resources.ApiKeys;
import net.atlasauth.atlas.resources.AttackProtection;
import net.atlasauth.atlas.resources.AuditLogs;
import net.atlasauth.atlas.resources.Billing;
import net.atlasauth.atlas.resources.Blocklist;
import net.atlasauth.atlas.resources.BotSignals;
import net.atlasauth.atlas.resources.Branding;
import net.atlasauth.atlas.resources.DataSubjectRequests;
import net.atlasauth.atlas.resources.Domains;
import net.atlasauth.atlas.resources.EmailTemplates;
import net.atlasauth.atlas.resources.Fga;
import net.atlasauth.atlas.resources.ImportExport;
import net.atlasauth.atlas.resources.Instance;
import net.atlasauth.atlas.resources.InstanceSecurity;
import net.atlasauth.atlas.resources.Invitations;
import net.atlasauth.atlas.resources.JwtTemplates;
import net.atlasauth.atlas.resources.Localizations;
import net.atlasauth.atlas.resources.LogStreams;
import net.atlasauth.atlas.resources.LtiPlatforms;
import net.atlasauth.atlas.resources.ManagedWaf;
import net.atlasauth.atlas.resources.Messaging;
import net.atlasauth.atlas.resources.NetworkAcls;
import net.atlasauth.atlas.resources.OAuthClients;
import net.atlasauth.atlas.resources.OAuthProviders;
import net.atlasauth.atlas.resources.Organizations;
import net.atlasauth.atlas.resources.Permissions;
import net.atlasauth.atlas.resources.RadiusClients;
import net.atlasauth.atlas.resources.RateLimitPolicy;
import net.atlasauth.atlas.resources.ResourceServers;
import net.atlasauth.atlas.resources.RiskBasedMfa;
import net.atlasauth.atlas.resources.Roles;
import net.atlasauth.atlas.resources.ScimProvisioning;
import net.atlasauth.atlas.resources.ScimTokens;
import net.atlasauth.atlas.resources.Sessions;
import net.atlasauth.atlas.resources.SignInTokens;
import net.atlasauth.atlas.resources.SmsTemplates;
import net.atlasauth.atlas.resources.SsoConnections;
import net.atlasauth.atlas.resources.SsoOnboarding;
import net.atlasauth.atlas.resources.Tokens;
import net.atlasauth.atlas.resources.Users;
import net.atlasauth.atlas.resources.Waitlist;
import net.atlasauth.atlas.resources.Webhooks;

import java.net.http.HttpClient;
import java.time.Duration;

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
    private final Branding branding;
    private final EmailTemplates emailTemplates;
    private final SmsTemplates smsTemplates;
    private final Localizations localizations;
    private final Actions actions;
    private final Billing billing;
    private final Messaging messaging;
    private final ImportExport importExport;
    private final DataSubjectRequests dataSubjectRequests;
    private final RadiusClients radiusClients;
    private final LtiPlatforms ltiPlatforms;
    private final Instance instance;
    private final InstanceSecurity instanceSecurity;
    private final Tokens tokens;

    private AtlasClient(Builder b) {
        if (b.secretKey == null || b.secretKey.isEmpty()) {
            throw new IllegalArgumentException("AtlasClient requires a secretKey (sk_...).");
        }
        String apiUrl = b.apiUrl != null ? b.apiUrl : DEFAULT_API_URL;
        HttpClient http = b.httpClient != null ? b.httpClient : HttpClient.newHttpClient();
        ObjectMapper mapper = b.mapper != null ? b.mapper : Json.mapper();
        Duration timeout = b.timeout != null ? b.timeout : Duration.ofSeconds(30);

        this.transport = new Transport(b.secretKey, apiUrl, http, mapper, timeout);

        this.users = new Users(transport);
        this.sessions = new Sessions(transport);
        this.organizations = new Organizations(transport);
        this.roles = new Roles(transport);
        this.permissions = new Permissions(transport);
        this.oauthClients = new OAuthClients(transport);
        this.resourceServers = new ResourceServers(transport);
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
        this.branding = new Branding(transport);
        this.emailTemplates = new EmailTemplates(transport);
        this.smsTemplates = new SmsTemplates(transport);
        this.localizations = new Localizations(transport);
        this.actions = new Actions(transport);
        this.billing = new Billing(transport);
        this.messaging = new Messaging(transport);
        this.importExport = new ImportExport(transport);
        this.dataSubjectRequests = new DataSubjectRequests(transport);
        this.radiusClients = new RadiusClients(transport);
        this.ltiPlatforms = new LtiPlatforms(transport);
        this.instance = new Instance(transport);
        this.instanceSecurity = new InstanceSecurity(transport);
        this.tokens = new Tokens(transport);
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

    public Tokens tokens() {
        return tokens;
    }

    /** Fluent builder for {@link AtlasClient}. Only {@code secretKey} is required. */
    public static final class Builder {
        private String secretKey;
        private String apiUrl;
        private HttpClient httpClient;
        private ObjectMapper mapper;
        private Duration timeout;

        /** The instance secret key ({@code sk_...}). Sent as a Bearer token, never logged. */
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
