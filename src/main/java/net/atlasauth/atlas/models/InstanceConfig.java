package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * The tenant's environment: its publishable key, frontend API host, allowed
 * origins and auth config. The signing private key is never returned.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceConfig(
        String object,
        String id,
        String environment,
        String publishableKey,
        String frontendApiHost,
        List<String> allowedOrigins,
        Map<String, Object> authConfig,
        long createdAt) {
}
