package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/audit_events/{id}/redact}. Required: fields, reason. */
public final class AuditEventSchemasRedactBody {
    /** Required. */
    public List<String> fields;
    /** Required. */
    public String reason;

    public AuditEventSchemasRedactBody() {
    }

    public AuditEventSchemasRedactBody fields(List<String> v) { this.fields = v; return this; }
    public AuditEventSchemasRedactBody reason(String v) { this.reason = v; return this; }
}
