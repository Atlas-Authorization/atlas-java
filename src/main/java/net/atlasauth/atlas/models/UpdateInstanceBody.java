package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/instance}. */
public final class UpdateInstanceBody {
    /** Exact origins only — a wildcard or a path is rejected. */
    public List<String> allowedOrigins;
    public Map<String, Object> authConfig;

    public UpdateInstanceBody allowedOrigins(List<String> v) { this.allowedOrigins = v; return this; }
    public UpdateInstanceBody authConfig(Map<String, Object> v) { this.authConfig = v; return this; }
}
