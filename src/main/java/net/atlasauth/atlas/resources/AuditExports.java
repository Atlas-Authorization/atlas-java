package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.http.Transport;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/audit_exports} — stream the audit log to your SIEM as CloudEvents. A body takes {@code name},
 * {@code type} (webhook ...), {@code destination} (for example {@code {"url": "..."}}), {@code backfill_hours},
 * {@code action_filter} and {@code enabled}; send {@code action_filter} as null to clear it.
 */
public final class AuditExports {
    private final Transport t;

    public AuditExports(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/audit_exports} — The exports. */
    public JsonNode list() {
        return t.get("/v1/audit_exports", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/audit_exports/{id}} — One export with its health. */
    public JsonNode get(String id) {
        return t.get("/v1/audit_exports/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/audit_exports} — Create an export (a signing secret is returned once). */
    public JsonNode create(Map<String, Object> body) {
        return create(body, null);
    }

    /** {@link #create(Map)} with an {@code Idempotency-Key}. */
    public JsonNode create(Map<String, Object> body, String idempotencyKey) {
        return t.post("/v1/audit_exports", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/audit_exports/{id}} — Change an export. */
    public JsonNode update(String id, Map<String, Object> body) {
        return t.patch("/v1/audit_exports/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/audit_exports/{id}} — Delete an export. */
    public JsonNode delete(String id) {
        return t.delete("/v1/audit_exports/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/audit_exports/{id}/test} — Send a test event to the destination. */
    public JsonNode test(String id) {
        return t.post("/v1/audit_exports/" + enc(id) + "/test", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/audit_exports/{id}/rotate_secret} — Rotate the signing secret (returned once). */
    public JsonNode rotateSecret(String id) {
        return t.post("/v1/audit_exports/" + enc(id) + "/rotate_secret", null, new TypeReference<>() {
        });
    }
}
