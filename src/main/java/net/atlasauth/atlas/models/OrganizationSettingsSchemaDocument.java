package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationSettingsSchemaDocument(
        String object,
        Map<String, Object> schema,
        Long version,
        Long updatedAt) {
}
