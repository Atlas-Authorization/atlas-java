package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/{id}/relay}. Required: url. */
public final class ConnectionsRelayBody {
    /** Must be in the connection's allowed_methods. */
    public String method;
    /** Absolute https URL on an allowed host. Required. */
    public String url;
    /** Extra request headers. Authorization, Cookie and Host cannot be set. */
    public Map<String, Object> headers;
    /** POST, PUT, PATCH or DELETE: a string, or an object sent as JSON (up to 256 KiB). */
    public Object body;
    /** Where the provider returns after authorizing, to receive meta.authorize_url on a 402. */
    public String redirectUrl;
    /** The user the authorization link is for (organization-owned connections). */
    public String connectUserId;

    public ConnectionsRelayBody() {
    }

    public ConnectionsRelayBody method(String v) { this.method = v; return this; }
    public ConnectionsRelayBody url(String v) { this.url = v; return this; }
    public ConnectionsRelayBody headers(Map<String, Object> v) { this.headers = v; return this; }
    public ConnectionsRelayBody body(Object v) { this.body = v; return this; }
    public ConnectionsRelayBody redirectUrl(String v) { this.redirectUrl = v; return this; }
    public ConnectionsRelayBody connectUserId(String v) { this.connectUserId = v; return this; }
}
