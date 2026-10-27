package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkflowsListResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        Boolean enabled,
        Map<String, Object> trigger,
        List<Map<String, Object>> steps,
        Long nextRunAt,
        Long createdAt,
        Long updatedAt) {
}
