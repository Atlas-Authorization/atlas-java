package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserOrganizationMembership(
        String object,
        String organizationId,
        String organizationSlug,
        String role,
        String plan,
        Long joinedAt) {
}
