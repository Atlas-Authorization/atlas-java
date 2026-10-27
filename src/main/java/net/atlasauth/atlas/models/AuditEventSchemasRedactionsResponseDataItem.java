package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditEventSchemasRedactionsResponseDataItem(
        String object,
        String id,
        String eventId,
        List<String> fields,
        String reason,
        String redactedBy,
        Long createdAt) {
}
