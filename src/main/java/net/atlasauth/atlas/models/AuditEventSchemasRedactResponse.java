package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/audit_events/{id}/redact}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditEventSchemasRedactResponse(
        String object,
        String eventId,
        List<String> redacted,
        List<String> unchanged,
        String reason) {
}
