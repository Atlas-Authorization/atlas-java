package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationSettings(
        String object,
        String organizationId,
        Map<String, Object> values,
        Long version,
        String updatedBy,
        Long updatedAt) {
}
