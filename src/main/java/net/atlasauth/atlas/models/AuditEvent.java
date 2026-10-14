package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditEvent(
        String object,
        String id,
        String organizationId,
        AuditEventActor actor,
        String action,
        AuditEventTarget target,
        long occurredAt,
        JsonNode metadata,
        long createdAt) {
}
