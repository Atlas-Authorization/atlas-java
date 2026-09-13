package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One recorded per-row failure inside a job's {@code errors} array. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportExportJobError(
        int index,
        String email,
        String code,
        String message) {
}
