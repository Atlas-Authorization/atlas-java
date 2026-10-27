package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/connections/oauth_clients}. Required: provider, clientId. */
public final class ConnectionsOauthClientsSetBody {
    public Map<String, Object> owner;
    /** Required. */
    public String provider;
    /** The provider environment the credential belongs to. sandbox selects the provider's test hosts (Salesforce test.salesforce.com, PayPal and QuickBooks sandboxes, DocuSign demo...). */
    public Object environment;
    /** Required. */
    public String clientId;
    /** Write-only. Required when the client is first created; omit it to keep the stored secret. */
    public String clientSecret;
    public List<String> scopes;
    /** Values that fill the provider's host templates for this one connection, for example &#123;"subdomain":"acme"&#125; (Zendesk), &#123;"shop":"acme"&#125; (Shopify), &#123;"domain":"acme.my.salesforce.com"&#125; (Salesforce My Domain), &#123;"data_center":"eu"&#125; (Zoho), &#123;"tenant":"contoso.onmicrosoft.com"&#125; (Microsoft), &#123;"host":"git.examp */
    public Map<String, Object> hostParams;
    public Boolean enabled;

    public ConnectionsOauthClientsSetBody() {
    }

    public ConnectionsOauthClientsSetBody owner(Map<String, Object> v) { this.owner = v; return this; }
    public ConnectionsOauthClientsSetBody provider(String v) { this.provider = v; return this; }
    public ConnectionsOauthClientsSetBody environment(Object v) { this.environment = v; return this; }
    public ConnectionsOauthClientsSetBody clientId(String v) { this.clientId = v; return this; }
    public ConnectionsOauthClientsSetBody clientSecret(String v) { this.clientSecret = v; return this; }
    public ConnectionsOauthClientsSetBody scopes(List<String> v) { this.scopes = v; return this; }
    public ConnectionsOauthClientsSetBody hostParams(Map<String, Object> v) { this.hostParams = v; return this; }
    public ConnectionsOauthClientsSetBody enabled(Boolean v) { this.enabled = v; return this; }
}
