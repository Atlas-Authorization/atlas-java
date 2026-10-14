package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AuditEventBody {
    public AuditEventActor actor;
    public String action;
    public AuditEventTarget target;
    public String organizationId;
    public Double occurredAt;
    public Map<String, Object> metadata;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AuditEventBody() {
    }

    public AuditEventBody actor(AuditEventActor v) { this.actor = v; return this; }
    public AuditEventBody action(String v) { this.action = v; return this; }
    public AuditEventBody target(AuditEventTarget v) { this.target = v; return this; }
    public AuditEventBody organizationId(String v) { this.organizationId = v; return this; }
    public AuditEventBody occurredAt(Double v) { this.occurredAt = v; return this; }
    public AuditEventBody metadata(Map<String, Object> v) { this.metadata = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AuditEventBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
