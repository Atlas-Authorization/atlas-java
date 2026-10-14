package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationEndUser(
        String object,
        String id,
        String organizationId,
        String userId,
        String status,
        String source,
        Map<String, Object> publicMetadata,
        Long approvedAt,
        Long createdAt,
        Long updatedAt) {
}
