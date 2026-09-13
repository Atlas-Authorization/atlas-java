package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * An OUTBOUND SCIM provisioning target — a downstream SCIM 2.0 endpoint Atlas
 * pushes its users/groups OUT to. The bearer token is write-only: stored
 * encrypted and never returned, so a read reports only {@code has_bearer_token}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScimProvisioningTarget(
        String object,
        String id,
        String name,
        String baseUrl,
        /** Whether a bearer is set — never the value. */
        boolean hasBearerToken,
        boolean enabled,
        Map<String, Object> attributeMapping,
        String deprovisionAction,
        String status,
        String cursor,
        int consecutiveFailures,
        String lastError,
        Long lastSyncedAt,
        long createdAt,
        long updatedAt) {
}
