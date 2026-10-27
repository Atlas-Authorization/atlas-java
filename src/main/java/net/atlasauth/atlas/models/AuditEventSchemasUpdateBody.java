package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/audit_event_schemas/{id}}. */
public final class AuditEventSchemasUpdateBody {
    public String description;
    public String status;

    public AuditEventSchemasUpdateBody() {
    }

    public AuditEventSchemasUpdateBody description(String v) { this.description = v; return this; }
    public AuditEventSchemasUpdateBody status(String v) { this.status = v; return this; }
}
