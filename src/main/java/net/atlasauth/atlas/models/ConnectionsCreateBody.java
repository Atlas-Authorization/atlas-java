package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections}. Required: owner, kind, provider, allowedHosts. */
public final class ConnectionsCreateBody {
    /** Required. */
    public Map<String, Object> owner;
    /** Required. */
    public Object kind;
    /** A built-in or custom OAuth provider key for oauth; any short key otherwise. Required. */
    public String provider;
    public String name;
    /** Hostnames, or *.example.com wildcards (1 to 20). Required. */
    public List<String> allowedHosts;
    /** api_key: placement (header or query), name, scheme. custom_header: name. hmac_secret: algorithm, encoding, signature_header, prefix, timestamp_header. client_credentials: token_url, client_id, scopes, auth_style, grant_type, account_id, audience, resource, extra_params, request_format, token_header. */
    public Map<String, Object> config;
    /** Relay verbs allowed for this connection (default GET, HEAD, POST). */
    public List<Object> allowedMethods;
    /** The provider environment the credential belongs to. sandbox selects the provider's test hosts (Salesforce test.salesforce.com, PayPal and QuickBooks sandboxes, DocuSign demo...). */
    public Object environment;
    /** Values that fill the provider's host templates for this one connection, for example &#123;"subdomain":"acme"&#125; (Zendesk), &#123;"shop":"acme"&#125; (Shopify), &#123;"domain":"acme.my.salesforce.com"&#125; (Salesforce My Domain), &#123;"data_center":"eu"&#125; (Zoho), &#123;"tenant":"contoso.onmicrosoft.com"&#125; (Microsoft), &#123;"host":"git.examp */
    public Map<String, Object> hostParams;
    /** Only data is accepted. A sign-in link is an external account. */
    public String purpose;

    public ConnectionsCreateBody() {
    }

    public ConnectionsCreateBody owner(Map<String, Object> v) { this.owner = v; return this; }
    public ConnectionsCreateBody kind(Object v) { this.kind = v; return this; }
    public ConnectionsCreateBody provider(String v) { this.provider = v; return this; }
    public ConnectionsCreateBody name(String v) { this.name = v; return this; }
    public ConnectionsCreateBody allowedHosts(List<String> v) { this.allowedHosts = v; return this; }
    public ConnectionsCreateBody config(Map<String, Object> v) { this.config = v; return this; }
    public ConnectionsCreateBody allowedMethods(List<Object> v) { this.allowedMethods = v; return this; }
    public ConnectionsCreateBody environment(Object v) { this.environment = v; return this; }
    public ConnectionsCreateBody hostParams(Map<String, Object> v) { this.hostParams = v; return this; }
    public ConnectionsCreateBody purpose(String v) { this.purpose = v; return this; }
}
