package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MachineWithSecret(
        String object,
        String id,
        String name,
        String organizationId,
        String ownerUserId,
        String status,
        Boolean ownerUsable,
        String effectiveStatus,
        String deviceKey,
        String publicKeyJkt,
        Map<String, Object> metadata,
        Long enrolledAt,
        long createdAt,
        Long updatedAt,
        String secret) {
}
