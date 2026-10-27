package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/directory_pull_sources}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectoryPullSourcesListResponse(
        String object,
        List<DirectoryPullSourcesListResponseDataItem> data,
        Boolean hasMore) {
}
