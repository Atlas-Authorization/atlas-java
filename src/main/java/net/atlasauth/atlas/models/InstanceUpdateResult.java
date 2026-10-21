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
        /** The declared integration per allowed origin. */ Map<String, String> allowedOriginIntegrations) {
    /** The 1.4 shape, without {@code allowedOriginIntegrations}. */
    public InstanceUpdateResult(String object, String id, List<String> allowedOrigins, Map<String, Object> authConfig) {
        this(object, id, allowedOrigins, authConfig, null);
    }
}
