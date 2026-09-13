package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Role(
        String object,
        String id,
        String key,
        String name,
        String description,
        boolean isSystem,
        List<String> permissions,
        int memberCount,
        long createdAt) {
}
