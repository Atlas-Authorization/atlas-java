package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFgaStore(
        String id,
        String name,
        String createdAt,
        String updatedAt) {
}
