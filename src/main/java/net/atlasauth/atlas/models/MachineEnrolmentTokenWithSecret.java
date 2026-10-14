package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MachineEnrolmentTokenWithSecret(
        String object,
        String id,
        String organizationId,
        long maxUses,
        long uses,
        long expiresAt,
        boolean requiresApproval,
        long createdAt,
        String token) {
}
