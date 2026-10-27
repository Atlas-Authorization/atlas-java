package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectoryPullSourcesListResponseDataItem(
        String object,
        String id,
        String organizationId,
        String name,
        String provider,
        String status,
        Map<String, Object> config,
        Map<String, Object> settings,
        String defaultRoleId,
        Boolean hasCredentials,
        Boolean hasUploadedCsv,
        Long nextSyncAt,
        Long createdAt,
        Long updatedAt) {
}
