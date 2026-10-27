package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AuditEventSchemasDeleteResponse;
import net.atlasauth.atlas.models.AuditEventSchemasListResponse;
import net.atlasauth.atlas.models.AuditEventSchemasListResponseDataItem;
import net.atlasauth.atlas.models.AuditEventSchemasRedactBody;
import net.atlasauth.atlas.models.AuditEventSchemasRedactResponse;
import net.atlasauth.atlas.models.AuditEventSchemasRedactionsResponse;
import net.atlasauth.atlas.models.AuditEventSchemasRegisterBody;
import net.atlasauth.atlas.models.AuditEventSchemasUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/audit_event_schemas} resource. */
public final class AuditEventSchemas {

    private final Transport t;

    public AuditEventSchemas(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/audit_event_schemas} - List the registered audit event schemas, by action then version (newest first). Scope: {@code audit_events:read}. */
    public AuditEventSchemasListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public AuditEventSchemasListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/audit_event_schemas", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/audit_event_schemas} - Register a schema for an action: the target types and actor types it allows and a JSON Schema its metadata must satisfy. `version` defaults to the next number for that action; a version is immutable (registering it again is a 409) - change a schema by registering a new version. From then on events with this action are validated on write. Scope: {@code audit_events:write}. */
    public AuditEventSchemasListResponseDataItem register(AuditEventSchemasRegisterBody body) {
        return register(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AuditEventSchemasListResponseDataItem register(AuditEventSchemasRegisterBody body, String idempotencyKey) {
        return t.request("POST", "/v1/audit_event_schemas", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/audit_event_schemas/{id}} - One audit event schema. Scope: {@code audit_events:read}. */
    public AuditEventSchemasListResponseDataItem get(String id) {
        return t.request("GET", "/v1/audit_event_schemas/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/audit_event_schemas/{id}} - Change a schema version's description or status. A deprecated version is no longer applied by default but still validates events that name it with `schema_version`. Scope: {@code audit_events:write}. */
    public AuditEventSchemasListResponseDataItem update(String id, AuditEventSchemasUpdateBody body) {
        return t.request("PATCH", "/v1/audit_event_schemas/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/audit_event_schemas/{id}} - Remove a schema version. Events already recorded keep their `schema_version` number. Scope: {@code audit_events:write}. */
    public AuditEventSchemasDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/audit_event_schemas/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/audit_events/{id}/redact} - Blank named fields of one audit event (replaced by [redacted]) without deleting it: `actor_id`, `target_id`, `target_name` or `metadata.&lt;dotted.path&gt;`. A `reason` is required. The redaction is recorded in its own log (GET /v1/audit_events/:id/redactions) and in Atlas's audit log as `audit_event.redacted` - the fields and the reason, never the removed values. Scope: {@code audit_events:write}. */
    public AuditEventSchemasRedactResponse redact(String id, AuditEventSchemasRedactBody body) {
        return redact(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AuditEventSchemasRedactResponse redact(String id, AuditEventSchemasRedactBody body, String idempotencyKey) {
        return t.request("POST", "/v1/audit_events/" + enc(id) + "/redact", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/audit_events/{id}/redactions} - An audit event's redaction history: which fields were blanked, why, and by which key. Scope: {@code audit_events:read}. */
    public AuditEventSchemasRedactionsResponse redactions(String id) {
        return redactions(id, null, null);
    }

    /** As above, with every optional parameter. */
    public AuditEventSchemasRedactionsResponse redactions(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/audit_events/" + enc(id) + "/redactions", q, null, null, false, new TypeReference<>() {
        });
    }
}
