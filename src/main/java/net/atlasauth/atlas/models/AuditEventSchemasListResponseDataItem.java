package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditEventSchemasListResponseDataItem(
        String object,
        String id,
        String action,
        Long version,
        String description,
        List<String> targetTypes,
        List<String> actorTypes,
        Map<String, Object> metadataSchema,
        String status,
        Long createdAt) {
}
