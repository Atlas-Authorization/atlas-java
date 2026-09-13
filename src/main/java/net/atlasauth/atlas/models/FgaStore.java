package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** An FGA store — a tenant-owned container of authorization models and tuples. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaStore(
        String object,
        String id,
        String name,
        long createdAt,
        long updatedAt) {
}
