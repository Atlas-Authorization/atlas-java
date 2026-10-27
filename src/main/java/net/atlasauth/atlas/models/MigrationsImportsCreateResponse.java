package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/migrations/{source}/imports}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsImportsCreateResponse(
        String object,
        String id,
        String source,
        String format,
        String status,
        String keyId,
        Long parts,
        Long bytes,
        String bundleSha256,
        Boolean bundlePurged,
        Map<String, Object> options,
        MigrationsImportsCreateResponseManifest manifest,
        Long total,
        MigrationsImportsCreateResponseProgress progress,
        MigrationsImportsCreateResponseReport report,
        MigrationsImportsCreateResponseResult result,
        MigrationsImportsCreateResponseError error,
        Long createdAt,
        Long updatedAt,
        Long analyzedAt,
        Long applyStartedAt,
        Long completedAt) {
}
