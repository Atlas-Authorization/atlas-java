package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * What a write answers with {@code ?dry_run=true}: the changes it would make, nothing changed.
 * {@code mode} is {@code native} (the endpoint validated in full) or {@code plan} (the body diffed
 * over the current object, unvalidated).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DryRunResult(
        String object,
        boolean dryRun,
        String mode,
        boolean validated,
        String method,
        String path,
        /** Each change: {@code section}, {@code before}, {@code after}. */ List<JsonNode> changes,
        JsonNode wouldReturn,
        String note) {
}
