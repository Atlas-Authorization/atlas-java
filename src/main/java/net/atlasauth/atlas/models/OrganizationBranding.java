package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationBranding(
        String object,
        String organizationId,
        String displayName,
        String logoUrl,
        String primaryColor,
        String supportUrl,
        String customDomain,
        Long updatedAt) {
}
