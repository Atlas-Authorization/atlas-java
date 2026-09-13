package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * The {@code GET /v1/waitlist_entries} response: a list envelope that also
 * carries per-status {@code counts} and cursor pagination.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WaitlistListResponse(
        String object,
        List<WaitlistEntry> data,
        Map<String, Integer> counts,
        boolean hasMore,
        String nextCursor) {
}
