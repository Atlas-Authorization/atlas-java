package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * The {@code GET /v1/users}-style cursor page: no total, a boolean
 * {@code has_more}, and an opaque {@code next_cursor} to pass back as
 * {@code starting_after}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CursorPage<T>(
        List<T> data,
        boolean hasMore,
        String nextCursor) {
}
