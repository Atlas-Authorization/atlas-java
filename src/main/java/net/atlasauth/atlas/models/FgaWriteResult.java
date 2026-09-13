package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The result of an FGA {@code write} — how many tuples were written and deleted. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaWriteResult(
        String object,
        int writes,
        int deletes) {
}
