package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/migrations/sources}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MigrationsSourcesResponse(
        String object,
        List<MigrationsSourcesResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
