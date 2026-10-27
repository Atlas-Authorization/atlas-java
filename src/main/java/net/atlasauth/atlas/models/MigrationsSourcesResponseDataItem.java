package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsSourcesResponseDataItem(
        String object,
        String source,
        String label,
        List<String> models,
        String passwords,
        List<String> exportVia,
        Map<String, Object> bridge,
        Map<String, Object> lazyMigration,
        List<String> passwordAlgorithms,
        String limits) {
}
