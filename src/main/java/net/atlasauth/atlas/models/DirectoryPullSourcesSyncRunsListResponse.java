package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/directory_pull_sources/{id}/sync_runs}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectoryPullSourcesSyncRunsListResponse(
        String object,
        List<DirectoryPullSourcesSyncRunsCreateResponse> data,
        Boolean hasMore) {
}
