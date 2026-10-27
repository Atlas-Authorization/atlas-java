package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/{id}/webhooks/verify}. Required: scheme, headers, rawBody. */
public final class ConnectionsWebhooksVerifyBody {
    /** Required. */
    public Object scheme;
    /** The delivery's headers. Required. */
    public Map<String, Object> headers;
    /** The body exactly as received (up to 1 MiB). Required. */
    public String rawBody;
    public Object rawBodyEncoding;
    /** hubspot_v3: the HTTP method of the delivery (default POST). */
    public String method;
    /** hubspot_v3: the full URL the provider posted to. */
    public String url;
    /** Use this webhook endpoint's signing secret and options. */
    public String endpointId;
    public Long toleranceSeconds;
    /** generic_hmac_sha256: signature_header, encoding, prefix, algorithm. */
    public Map<String, Object> options;
    /** Set false to check without remembering the delivery (default true). */
    public Boolean recordReplay;

    public ConnectionsWebhooksVerifyBody() {
    }

    public ConnectionsWebhooksVerifyBody scheme(Object v) { this.scheme = v; return this; }
    public ConnectionsWebhooksVerifyBody headers(Map<String, Object> v) { this.headers = v; return this; }
    public ConnectionsWebhooksVerifyBody rawBody(String v) { this.rawBody = v; return this; }
    public ConnectionsWebhooksVerifyBody rawBodyEncoding(Object v) { this.rawBodyEncoding = v; return this; }
    public ConnectionsWebhooksVerifyBody method(String v) { this.method = v; return this; }
    public ConnectionsWebhooksVerifyBody url(String v) { this.url = v; return this; }
    public ConnectionsWebhooksVerifyBody endpointId(String v) { this.endpointId = v; return this; }
    public ConnectionsWebhooksVerifyBody toleranceSeconds(Long v) { this.toleranceSeconds = v; return this; }
    public ConnectionsWebhooksVerifyBody options(Map<String, Object> v) { this.options = v; return this; }
    public ConnectionsWebhooksVerifyBody recordReplay(Boolean v) { this.recordReplay = v; return this; }
}
