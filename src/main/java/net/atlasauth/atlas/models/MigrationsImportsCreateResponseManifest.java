package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsImportsCreateResponseManifest(
        String exportedAt,
        Map<String, Object> exporter,
        String origin,
        String sourceVersion,
        List<String> models,
        List<String> providers,
        String rpId,
        List<String> warnings) {
}
