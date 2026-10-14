package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateDataSubjectRequestBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DataSubjectRequest;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/data_subject_requests} (GDPR/DSAR) resource. */
public final class DataSubjectRequests {

    private final Transport t;

    public DataSubjectRequests(Transport t) {
        this.t = t;
    }

    public CursorPage<DataSubjectRequest> list() {
        return list(null, null, null, null, null);
    }

    public CursorPage<DataSubjectRequest> list(Integer limit, String startingAfter, String type, String status,
                                               String userId) {
        Map<String, Object> q = Params.of()
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .set("type", type)
                .set("status", status)
                .set("user_id", userId)
                .map();
        return t.get("/v1/data_subject_requests", q, new TypeReference<>() {
        });
    }

    public DataSubjectRequest get(String id) {
        return t.get("/v1/data_subject_requests/" + enc(id), new TypeReference<>() {
        });
    }

    public DataSubjectRequest fulfill(String id) {
        return fulfill(id, null);
    }

    /** Fulfil a request now — build the export package, or run the erasure — ahead of schedule. */
    public DataSubjectRequest fulfill(String id, String idempotencyKey) {
        return t.post("/v1/data_subject_requests/" + enc(id) + "/fulfill", null, idempotencyKey,
                new TypeReference<>() {
                });
    }

    public DataSubjectRequest reject(String id) {
        return reject(id, null, null);
    }

    public DataSubjectRequest reject(String id, String reason) {
        return reject(id, reason, null);
    }

    /** Reject a request with a recorded reason. Terminal — it cannot be re-actioned. */
    public DataSubjectRequest reject(String id, String reason, String idempotencyKey) {
        Map<String, Object> body = Params.of().set("reason", reason).map();
        return t.post("/v1/data_subject_requests/" + enc(id) + "/reject", body, idempotencyKey,
                new TypeReference<>() {
                });
    }

    /** {@code POST /v1/data_subject_requests} — Create a DSAR (export or erasure) for a user from the backend — the twin of the self-service /client/me flows, for a DSAR orchestrator. */
    public DataSubjectRequest create(CreateDataSubjectRequestBody body) {
        return t.post("/v1/data_subject_requests", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public DataSubjectRequest create(CreateDataSubjectRequestBody body, String idempotencyKey) {
        return t.post("/v1/data_subject_requests", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/data_subject_requests/{id}/cancel} — Cancel a scheduled erasure within its grace window — the sibling to /fulfill (which expedites). */
    public DataSubjectRequest cancel(String id) {
        return t.post("/v1/data_subject_requests/" + enc(id) + "/cancel", null, new TypeReference<>() {
        });
    }
}
