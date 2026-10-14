package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListAuditEventsParams {
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
    public Long limit;

    public ListAuditEventsParams() {
    }

    public ListAuditEventsParams organizationId(String v) { this.organizationId = v; return this; }
    public ListAuditEventsParams actorId(String v) { this.actorId = v; return this; }
    public ListAuditEventsParams action(String v) { this.action = v; return this; }
    public ListAuditEventsParams targetType(String v) { this.targetType = v; return this; }
    public ListAuditEventsParams targetId(String v) { this.targetId = v; return this; }
    public ListAuditEventsParams from(Long v) { this.from = v; return this; }
    public ListAuditEventsParams to(Long v) { this.to = v; return this; }
    public ListAuditEventsParams anyOf(Boolean v) { this.anyOf = v; return this; }
    public ListAuditEventsParams metadataKey(String v) { this.metadataKey = v; return this; }
    public ListAuditEventsParams metadataValue(String v) { this.metadataValue = v; return this; }
    public ListAuditEventsParams startingAfter(String v) { this.startingAfter = v; return this; }
    public ListAuditEventsParams limit(Long v) { this.limit = v; return this; }

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
                .set("limit", limit)
                .map();
    }
}
