package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AuditEvent;
import net.atlasauth.atlas.models.AuditEventBatchBody;
import net.atlasauth.atlas.models.AuditEventBody;
import net.atlasauth.atlas.models.AuditEventsDeleted;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.ExportAuditEventsParams;
import net.atlasauth.atlas.models.ListAuditEventsParams;
import net.atlasauth.atlas.models.ListPage;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Your own custom audit events ({@code /v1/audit_events}). */
public final class AuditEvents {

    private final Transport t;

    public AuditEvents(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/audit_events} — Write one of the tenant's OWN custom audit events (actor, action, optional target, optional metadata). */
    public AuditEvent create(AuditEventBody body) {
        return t.post("/v1/audit_events", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public AuditEvent create(AuditEventBody body, String idempotencyKey) {
        return t.post("/v1/audit_events", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public CursorPage<AuditEvent> list() {
        return list(null);
    }

    /** {@code GET /v1/audit_events} — Query the tenant's custom audit events, filterable by organization_id, actor_id, action, target_type, target_id and an occurred-at window (from/to), cursor-paginated with limit + starting_after. */
    public CursorPage<AuditEvent> list(ListAuditEventsParams params) {
        return t.get("/v1/audit_events", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }

    public AuditEventsDeleted deleteAll() {
        return deleteAll(null, null);
    }

    /** {@code DELETE /v1/audit_events} — Bulk-delete the instance's custom audit events — a TEST-MODE reset, allowed ONLY on a development instance (a production audit trail cannot be bulk-erased: 403). */
    public AuditEventsDeleted deleteAll(String organizationId, String action) {
        Map<String, Object> q = Params.of().set("organization_id", organizationId).set("action", action).map();
        return t.delete("/v1/audit_events", q, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/audit_events/batch} — Write up to 100 custom audit events in one call. */
    public ListPage<AuditEvent> createBatch(AuditEventBatchBody body) {
        return t.post("/v1/audit_events/batch", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public ListPage<AuditEvent> createBatch(AuditEventBatchBody body, String idempotencyKey) {
        return t.post("/v1/audit_events/batch", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public String export() {
        return export(null);
    }

    /** {@code GET /v1/audit_events/export} — Bulk-export every matching custom audit event in one streamed response — `format=jsonl` (newline-delimited JSON, default) or `format=csv`. */
    public String export(ExportAuditEventsParams params) {
        return t.getRaw("/v1/audit_events/export", params == null ? null : params.toQuery());
    }
}
