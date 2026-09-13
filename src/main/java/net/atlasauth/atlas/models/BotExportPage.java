package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A newest-first export page: {@code nextBefore} is the created-at epoch-ms cursor
 * to pass back as {@code before} for the next (older) page, or {@code null} at the end.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BotExportPage<T>(
        String object,
        List<T> data,
        Long nextBefore) {
}
