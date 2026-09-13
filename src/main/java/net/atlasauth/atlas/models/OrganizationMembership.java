package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationMembership(
        String object,
        String id,
        String organizationId,
        String userId,
        String role,
        long createdAt) {
}
