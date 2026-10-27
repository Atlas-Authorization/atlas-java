package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/{id}/connect}. Required: redirectUrl. */
public final class ConnectionsConnectBody {
    /** Where the owner lands after the provider returns. Must be an allowed origin of the instance. Required. */
    public String redirectUrl;
    /** Values that fill the provider's host templates for this one connection, for example &#123;"subdomain":"acme"&#125; (Zendesk), &#123;"shop":"acme"&#125; (Shopify), &#123;"domain":"acme.my.salesforce.com"&#125; (Salesforce My Domain), &#123;"data_center":"eu"&#125; (Zoho), &#123;"tenant":"contoso.onmicrosoft.com"&#125; (Microsoft), &#123;"host":"git.examp */
    public Map<String, Object> hostParams;
    /** The provider environment the credential belongs to. sandbox selects the provider's test hosts (Salesforce test.salesforce.com, PayPal and QuickBooks sandboxes, DocuSign demo...). */
    public Object environment;
    /** Scopes to ask for on top of the provider defaults, the owner client's scopes and config.scopes. */
    public List<String> additionalScopes;
    public String loginHint;
    /** popup completes through postMessage like a popup sign-in. */
    public Object display;
    /** When the provider returns a different account than the one this connection already holds, create a sibling connection for it instead of answering 409. */
    public Boolean createIfNew;
    /** Informational: the user who is connecting (stored as authorized_by_user_id). */
    public String connectUserId;

    public ConnectionsConnectBody() {
    }

    public ConnectionsConnectBody redirectUrl(String v) { this.redirectUrl = v; return this; }
    public ConnectionsConnectBody hostParams(Map<String, Object> v) { this.hostParams = v; return this; }
    public ConnectionsConnectBody environment(Object v) { this.environment = v; return this; }
    public ConnectionsConnectBody additionalScopes(List<String> v) { this.additionalScopes = v; return this; }
    public ConnectionsConnectBody loginHint(String v) { this.loginHint = v; return this; }
    public ConnectionsConnectBody display(Object v) { this.display = v; return this; }
    public ConnectionsConnectBody createIfNew(Boolean v) { this.createIfNew = v; return this; }
    public ConnectionsConnectBody connectUserId(String v) { this.connectUserId = v; return this; }
}
