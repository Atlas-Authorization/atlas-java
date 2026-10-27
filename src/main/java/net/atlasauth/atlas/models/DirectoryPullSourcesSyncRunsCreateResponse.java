package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/directory_pull_sources/{id}/sync_runs}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectoryPullSourcesSyncRunsCreateResponse(
        String object,
        String id,
        String sourceId,
        String organizationId,
        String trigger,
        String status,
        Map<String, Object> stats,
        String error,
        Long createdAt,
        Long startedAt,
        Long finishedAt) {
}
