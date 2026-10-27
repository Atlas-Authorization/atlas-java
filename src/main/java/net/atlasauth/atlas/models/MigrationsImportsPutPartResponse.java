package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code PUT /v1/migrations/{source}/imports/{id}/parts/{part}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsImportsPutPartResponse(
        String object,
        String importId,
        Long part,
        Long bytes,
        String sha256) {
}
