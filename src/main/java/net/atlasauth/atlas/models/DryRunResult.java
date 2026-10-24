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
        String note,
        /** The same non-fatal warnings the real write returns (each has a code). */ List<JsonNode> warnings,
        /** The status the real write would answer with (rollback mode). */ Integer wouldReturnStatus,
        /** Side effects held back: email, sms, voice, push, outbound_network, redis, cache, file. */ List<String> suppressed,
        /** Plan mode: why validation did not run to the end. */ DryRunValidationSkipped validationSkipped) {
    /** The shape before warnings, wouldReturnStatus, suppressed, validationSkipped. */
    public DryRunResult(String object, boolean dryRun, String mode, boolean validated, String method, String path, List<JsonNode> changes, JsonNode wouldReturn, String note) {
        this(object, dryRun, mode, validated, method, path, changes, wouldReturn, note, null, null, null, null);
    }

}
