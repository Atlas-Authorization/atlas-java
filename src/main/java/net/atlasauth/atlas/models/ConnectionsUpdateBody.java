package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/connections/{id}}. */
public final class ConnectionsUpdateBody {
    public String name;
    public List<String> allowedHosts;
    public Map<String, Object> config;
    public String sharedByUserId;
    /** Transfer the connection to another organization or user of this instance. Its tokens move with it; nobody reconnects. */
    public Map<String, Object> owner;
    /** The provider environment the credential belongs to. sandbox selects the provider's test hosts (Salesforce test.salesforce.com, PayPal and QuickBooks sandboxes, DocuSign demo...). */
    public Object environment;
    /** Values that fill the provider's host templates for this one connection, for example &#123;"subdomain":"acme"&#125; (Zendesk), &#123;"shop":"acme"&#125; (Shopify), &#123;"domain":"acme.my.salesforce.com"&#125; (Salesforce My Domain), &#123;"data_center":"eu"&#125; (Zoho), &#123;"tenant":"contoso.onmicrosoft.com"&#125; (Microsoft), &#123;"host":"git.examp */
    public Map<String, Object> hostParams;
    public List<Object> allowedMethods;

    public ConnectionsUpdateBody() {
    }

    public ConnectionsUpdateBody name(String v) { this.name = v; return this; }
    public ConnectionsUpdateBody allowedHosts(List<String> v) { this.allowedHosts = v; return this; }
    public ConnectionsUpdateBody config(Map<String, Object> v) { this.config = v; return this; }
    public ConnectionsUpdateBody sharedByUserId(String v) { this.sharedByUserId = v; return this; }
    public ConnectionsUpdateBody owner(Map<String, Object> v) { this.owner = v; return this; }
    public ConnectionsUpdateBody environment(Object v) { this.environment = v; return this; }
    public ConnectionsUpdateBody hostParams(Map<String, Object> v) { this.hostParams = v; return this; }
    public ConnectionsUpdateBody allowedMethods(List<Object> v) { this.allowedMethods = v; return this; }
}
