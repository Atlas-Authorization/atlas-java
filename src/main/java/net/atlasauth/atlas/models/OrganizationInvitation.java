package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationInvitation(
        String object,
        String id,
        String organizationId,
        String email,
        String role,
        String status,
        String inviterUserId,
        long expiresAt,
        long createdAt) {
}
