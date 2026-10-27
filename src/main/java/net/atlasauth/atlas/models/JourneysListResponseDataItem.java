package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneysListResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        Boolean isDefault,
        List<String> applications,
        Boolean enabled,
        Long activeVersion,
        Long latestVersion,
        Map<String, Object> definition,
        Long createdAt,
        Long updatedAt) {
}
