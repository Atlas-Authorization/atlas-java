package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** {@code PATCH /v1/instance} echoes only the mutable fields, not a full instance. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceUpdateResult(
        String object,
        String id,
        List<String> allowedOrigins,
        Map<String, Object> authConfig,
        /** The declared integration per allowed origin. */ Map<String, String> allowedOriginIntegrations,
        /** What the write did not do as sent; empty or null when it did everything. */ List<InstanceUpdateWarning> warnings) {
    /** The 1.18 shape, without {@code warnings}. */
    public InstanceUpdateResult(String object, String id, List<String> allowedOrigins, Map<String, Object> authConfig, Map<String, String> allowedOriginIntegrations) {
        this(object, id, allowedOrigins, authConfig, allowedOriginIntegrations, null);
    }

    /** The 1.4 shape, without {@code allowedOriginIntegrations}. */
    public InstanceUpdateResult(String object, String id, List<String> allowedOrigins, Map<String, Object> authConfig) {
        this(object, id, allowedOrigins, authConfig, null, null);
    }
}
