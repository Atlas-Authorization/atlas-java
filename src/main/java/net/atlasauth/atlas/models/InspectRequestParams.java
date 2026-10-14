package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class InspectRequestParams {
    public String origin;
    public String method;
    public String path;
    public String token;

    public InspectRequestParams() {
    }

    public InspectRequestParams origin(String v) { this.origin = v; return this; }
    public InspectRequestParams method(String v) { this.method = v; return this; }
    public InspectRequestParams path(String v) { this.path = v; return this; }
    public InspectRequestParams token(String v) { this.token = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("origin", origin)
                .set("method", method)
                .set("path", path)
                .set("token", token)
                .map();
    }
}
