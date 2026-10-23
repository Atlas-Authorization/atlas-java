package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * A Better Auth import ({@code object: "better_auth_import"}).
 *
 * <p>Statuses: {@code uploading → analyzing → ready | invalid → queued → applying → completed | failed}.
 * A failed apply keeps its cursor, so applying again resumes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BetterAuthImport(
        String object,
        String id,
        String status,
        String keyId,
        Long parts,
        Long bytes,
        String bundleSha256,
        Boolean bundlePurged,
        Map<String, Object> options,
        Source source,
        Long total,
        Progress progress,
        BetterAuthImportReport report,
        BetterAuthImportReport result,
        Error error,
        Long createdAt,
        Long updatedAt,
        Long analyzedAt,
        Long applyStartedAt,
        Long completedAt) {

    /** True while Atlas is working on the import ({@code analyzing}, {@code queued}, {@code applying}). */
    public boolean inFlight() {
        return "analyzing".equals(status) || "queued".equals(status) || "applying".equals(status);
    }

    /** What the exporter reported about the Better Auth app. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Source(
            String exportedAt,
            Map<String, Object> exporter,
            String betterAuthVersion,
            String baseUrl,
            List<String> plugins,
            List<String> models,
            String rpId,
            List<String> socialProviders) {
    }

    /** Live progress of an apply. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Progress(Long total, Long processed, Long succeeded, Long failed, Long percent) {
    }

    /** Why an import is {@code invalid} or {@code failed} ({@code bundle_wrong_key}, {@code apply_failed}, ...). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Error(String code, String message) {
    }
}
