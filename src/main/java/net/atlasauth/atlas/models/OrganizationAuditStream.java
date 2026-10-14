package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationAuditStream(
        String object,
        String id,
        String organizationId,
        String url,
        String format,
        Boolean active,
        Boolean hasSecret,
        Long createdAt) {
}
