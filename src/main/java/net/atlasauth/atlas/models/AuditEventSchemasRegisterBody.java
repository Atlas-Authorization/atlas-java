package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/audit_event_schemas}. Required: action. */
public final class AuditEventSchemasRegisterBody {
    /** e.g. invoice.sent Required. */
    public String action;
    public Long version;
    public String description;
    public List<String> targetTypes;
    public List<String> actorTypes;
    public Map<String, Object> metadataSchema;

    public AuditEventSchemasRegisterBody() {
    }

    public AuditEventSchemasRegisterBody action(String v) { this.action = v; return this; }
    public AuditEventSchemasRegisterBody version(Long v) { this.version = v; return this; }
    public AuditEventSchemasRegisterBody description(String v) { this.description = v; return this; }
    public AuditEventSchemasRegisterBody targetTypes(List<String> v) { this.targetTypes = v; return this; }
    public AuditEventSchemasRegisterBody actorTypes(List<String> v) { this.actorTypes = v; return this; }
    public AuditEventSchemasRegisterBody metadataSchema(Map<String, Object> v) { this.metadataSchema = v; return this; }
}
