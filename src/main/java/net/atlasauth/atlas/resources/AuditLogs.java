package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AuditLog;
import net.atlasauth.atlas.models.CursorPage;

import java.util.Map;

/** The {@code /v1/audit_logs} resource. */
public final class AuditLogs {

    private final Transport t;

    public AuditLogs(Transport t) {
        this.t = t;
    }

    public CursorPage<AuditLog> list() {
        return list(null, null, null, null);
    }

    /**
     * {@code GET /v1/audit_logs} — the instance's audit trail, newest first.
     *
     * @param actorId filter to one actor.
     * @param action  filter to one action name.
     */
    public CursorPage<AuditLog> list(Integer limit, String startingAfter, String actorId, String action) {
        Map<String, Object> q = Params.of()
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .set("actor_id", actorId)
                .set("action", action)
                .map();
        return t.get("/v1/audit_logs", q, new TypeReference<>() {
        });
    }
}
