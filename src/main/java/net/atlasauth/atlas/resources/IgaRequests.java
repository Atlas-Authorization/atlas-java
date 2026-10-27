package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IgaRequestsCreateBody;
import net.atlasauth.atlas.models.IgaRequestsDecideBody;
import net.atlasauth.atlas.models.IgaRequestsListResponse;
import net.atlasauth.atlas.models.IgaRequestsListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/iga} resource. */
public final class IgaRequests {

    private final Transport t;

    public IgaRequests(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/iga/requests} - List access requests, newest first. Scope: {@code iga:read}. */
    public IgaRequestsListResponse list() {
        return list(null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaRequestsListResponse list(String status, String userId, String requesterUserId, String entitlement, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("status", status).set("user_id", userId).set("requester_user_id", requesterUserId).set("entitlement", entitlement).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/requests", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/requests} - File an access request on behalf of a user. It follows the entitlement's approval steps (and any extra step a separation-of-duties rule adds); with no steps it is fulfilled at once. A blocking separation-of-duties conflict, a user who already holds the access, or a pending duplicate is a 409. `duration_seconds` makes the grant time-bound (default and maximum: the entitlement's limit). Scope: {@code iga:write}. */
    public IgaRequestsListResponseDataItem create(IgaRequestsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaRequestsListResponseDataItem create(IgaRequestsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/requests", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/requests/{id}} - Read one request with its steps and the approvals recorded so far. Scope: {@code iga:read}. */
    public IgaRequestsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/iga/requests/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/requests/{id}/decide} - Decide the open step. With `approver_user_id` the decision is recorded as that person (who must be an approver of the step or hold a delegation); without it the call is an administrator override that settles the step. A denial ends the request. 409 when it is no longer pending. Scope: {@code iga:write}. */
    public IgaRequestsListResponseDataItem decide(String id, IgaRequestsDecideBody body) {
        return decide(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaRequestsListResponseDataItem decide(String id, IgaRequestsDecideBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/requests/" + enc(id) + "/decide", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/requests/{id}/cancel} - Cancel a pending request. Scope: {@code iga:write}. */
    public IgaRequestsListResponseDataItem cancel(String id) {
        return cancel(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaRequestsListResponseDataItem cancel(String id, String idempotencyKey) {
        return t.request("POST", "/v1/iga/requests/" + enc(id) + "/cancel", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/approvals} - The requests waiting on one person's decision, including those delegated to them. Scope: {@code iga:read}. */
    public IgaRequestsListResponse inbox(String approverUserId) {
        return inbox(approverUserId, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaRequestsListResponse inbox(String approverUserId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("approver_user_id", approverUserId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/approvals", q, null, null, false, new TypeReference<>() {
        });
    }
}
