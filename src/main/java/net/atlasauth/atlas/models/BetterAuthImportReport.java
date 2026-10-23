package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * The dry-run {@code report} (what an apply would do: {@code willCreate} / {@code willUpdate} /
 * {@code willSkip}) or the apply {@code result} (what it wrote: {@code created} / {@code updated} /
 * {@code unchanged} / {@code skipped} / {@code failed}, plus {@code completedAt}) of a Better Auth import.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BetterAuthImportReport(
        List<ModelCount> models,
        List<Skipped> skipped,
        Long skippedTotal,
        Map<String, Long> skippedByCode,
        List<Conflict> conflicts,
        Long conflictsTotal,
        List<ActionItem> actionItems,
        List<String> warnings,
        Map<String, Object> notes,
        Long completedAt) {

    /** One Better Auth model's counts. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ModelCount(
            String model,
            String label,
            Long found,
            Long willCreate,
            Long willUpdate,
            Long willSkip,
            Long created,
            Long updated,
            Long unchanged,
            Long skipped,
            Long failed) {
    }

    /** A skipped row. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Skipped(String model, String rowId, String reasonCode, String reason) {
    }

    /** A conflict ({@code resolution}: {@code merge} or {@code skip}). Apply needs {@code confirm_conflicts}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Conflict(String model, String rowId, String code, String message, String resolution,
                           String existingId, String value) {
    }

    /**
     * Something to do ({@code severity}: {@code required} means fix it and re-run, or the rows it names stay
     * out; {@code recommended}; {@code info}). The API never blocks an apply on an action item.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ActionItem(String code, String severity, String message, Long count, List<String> items) {
    }
}
