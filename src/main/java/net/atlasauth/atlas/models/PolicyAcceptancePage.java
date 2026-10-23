package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A page of {@link PolicyAcceptance}s; continue with {@code nextCursor} as {@code starting_after}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyAcceptancePage(
        String object,
        List<PolicyAcceptance> data,
        Boolean hasMore,
        String nextCursor) {
}
