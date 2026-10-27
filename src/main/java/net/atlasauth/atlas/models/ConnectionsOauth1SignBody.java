package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/{id}/oauth1/sign}. Required: url. */
public final class ConnectionsOauth1SignBody {
    /** Default GET. */
    public String method;
    /** Required. */
    public String url;
    /** The request body, to sign when it is form-encoded. */
    public String body;
    public String contentType;

    public ConnectionsOauth1SignBody() {
    }

    public ConnectionsOauth1SignBody method(String v) { this.method = v; return this; }
    public ConnectionsOauth1SignBody url(String v) { this.url = v; return this; }
    public ConnectionsOauth1SignBody body(String v) { this.body = v; return this; }
    public ConnectionsOauth1SignBody contentType(String v) { this.contentType = v; return this; }
}
