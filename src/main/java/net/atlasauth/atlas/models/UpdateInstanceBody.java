package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Body for {@code PATCH /v1/instance}. {@code authConfig} also takes
 * {@code strictRequestBodies}, {@code dangerouslyReportSkippedEmailsAsVerified} and
 * {@code branding.audiences}.
 */
public final class UpdateInstanceBody {
    /** Exact origins only — a wildcard or a path is rejected. */
    public List<String> allowedOrigins;
    public Map<String, Object> authConfig;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateInstanceBody allowedOrigins(List<String> v) { this.allowedOrigins = v; return this; }
    public UpdateInstanceBody authConfig(Map<String, Object> v) { this.authConfig = v; return this; }

    /**
     * Send {@code allowed_origins} as entries that may also declare how each origin signs in
     * ({@link AllowedOriginEntry}); replaces {@link #allowedOrigins(List)}.
     */
    public UpdateInstanceBody allowedOriginEntries(List<AllowedOriginEntry> v) {
        this.allowedOrigins = null;
        this.additionalFields.put("allowed_origins", v);
        return this;
    }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public UpdateInstanceBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
