package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/{id}/webhook_endpoints}. Required: scheme, forwardUrl. */
public final class ConnectionsWebhooksCreateEndpointBody {
    /** Required. */
    public Object scheme;
    /** https URL that receives verified deliveries as Atlas-signed webhooks. Required. */
    public String forwardUrl;
    /** Write-only. The provider's signing secret for this endpoint (Slack signing secret, Zoom secret token, Calendly signing key...). Without it, the connection's hmac_secret credential or its OAuth client secret is used. */
    public String signingSecret;
    /** generic_hmac_sha256 only: signature_header, encoding (hex or base64), prefix, algorithm. */
    public Map<String, Object> options;
    public Boolean enabled;

    public ConnectionsWebhooksCreateEndpointBody() {
    }

    public ConnectionsWebhooksCreateEndpointBody scheme(Object v) { this.scheme = v; return this; }
    public ConnectionsWebhooksCreateEndpointBody forwardUrl(String v) { this.forwardUrl = v; return this; }
    public ConnectionsWebhooksCreateEndpointBody signingSecret(String v) { this.signingSecret = v; return this; }
    public ConnectionsWebhooksCreateEndpointBody options(Map<String, Object> v) { this.options = v; return this; }
    public ConnectionsWebhooksCreateEndpointBody enabled(Boolean v) { this.enabled = v; return this; }
}
