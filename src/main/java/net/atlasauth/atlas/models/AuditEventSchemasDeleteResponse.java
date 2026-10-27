package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/audit_event_schemas/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditEventSchemasDeleteResponse(
        String object,
        String id,
        Boolean deleted) {
}
