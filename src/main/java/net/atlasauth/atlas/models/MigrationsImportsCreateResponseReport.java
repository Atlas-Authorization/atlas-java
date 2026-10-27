package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsImportsCreateResponseReport(
        List<Map<String, Object>> models,
        Map<String, Object> passwordAlgorithms,
        List<Map<String, Object>> skipped,
        Long skippedTotal,
        Map<String, Object> skippedByCode,
        List<Map<String, Object>> conflicts,
        Long conflictsTotal,
        List<Map<String, Object>> actionItems,
        List<String> warnings,
        Map<String, Object> notes) {
}
