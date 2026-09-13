package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Permission(
        String object,
        String id,
        String key,
        String name,
        String description,
        boolean isSystem) {
}
