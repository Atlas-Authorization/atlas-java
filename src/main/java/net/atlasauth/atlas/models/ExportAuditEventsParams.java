package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ExportAuditEventsParams {
    public String organizationId;
    public String actorId;
    public String action;
    public String targetType;
    public String targetId;
    public Long from;
    public Long to;
    public Boolean anyOf;
    public String metadataKey;
    public String metadataValue;
    public String startingAfter;
    public String format;

    public ExportAuditEventsParams() {
    }

    public ExportAuditEventsParams organizationId(String v) { this.organizationId = v; return this; }
    public ExportAuditEventsParams actorId(String v) { this.actorId = v; return this; }
    public ExportAuditEventsParams action(String v) { this.action = v; return this; }
    public ExportAuditEventsParams targetType(String v) { this.targetType = v; return this; }
    public ExportAuditEventsParams targetId(String v) { this.targetId = v; return this; }
    public ExportAuditEventsParams from(Long v) { this.from = v; return this; }
    public ExportAuditEventsParams to(Long v) { this.to = v; return this; }
    public ExportAuditEventsParams anyOf(Boolean v) { this.anyOf = v; return this; }
    public ExportAuditEventsParams metadataKey(String v) { this.metadataKey = v; return this; }
    public ExportAuditEventsParams metadataValue(String v) { this.metadataValue = v; return this; }
    public ExportAuditEventsParams startingAfter(String v) { this.startingAfter = v; return this; }
    public ExportAuditEventsParams format(String v) { this.format = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("organization_id", organizationId)
                .set("actor_id", actorId)
                .set("action", action)
                .set("target_type", targetType)
                .set("target_id", targetId)
                .set("from", from)
                .set("to", to)
                .set("any_of", anyOf)
                .set("metadata_key", metadataKey)
                .set("metadata_value", metadataValue)
                .set("starting_after", startingAfter)
                .set("format", format)
                .map();
    }
}
