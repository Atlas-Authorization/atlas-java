package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Organization(
        String object,
        String id,
        String name,
        String slug,
        String imageUrl,
        Map<String, Object> publicMetadata,
        int maxAllowedMemberships,
        String createdBy,
        long createdAt,
        long updatedAt) {
}
