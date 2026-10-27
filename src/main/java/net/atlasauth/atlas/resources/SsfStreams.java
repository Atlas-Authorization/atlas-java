package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.SsfStreamsCreateBody;
import net.atlasauth.atlas.models.SsfStreamsEventsResponse;
import net.atlasauth.atlas.models.SsfStreamsGetStatusResponse;
import net.atlasauth.atlas.models.SsfStreamsListResponse;
import net.atlasauth.atlas.models.SsfStreamsListResponseDataItem;
import net.atlasauth.atlas.models.SsfStreamsUpdateBody;
import net.atlasauth.atlas.models.SsfStreamsUpdateStatusBody;
import net.atlasauth.atlas.models.SsfStreamsVerifyBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/ssf} resource. */
public final class SsfStreams {

    private final Transport t;

    public SsfStreams(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/ssf/streams} - List the Shared Signals streams this instance transmits on (push and poll), with their delivery method, requested events and status. Scope: {@code ssf:read}. */
    public SsfStreamsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public SsfStreamsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/ssf/streams", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/ssf/streams} - Create a stream. `delivery_method` is `push` (we POST each signed SET to `endpoint_url`, with the optional `authorization_header`) or `poll` (the receiver pulls from the returned `poll_endpoint` with the one-time `poll_token`). `events_requested` limits the events sent (empty = all supported). SETs are signed with the instance signing key. Scope: {@code ssf:write}. */
    public JsonNode create(SsfStreamsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode create(SsfStreamsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/ssf/streams", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/ssf/streams/{id}} - Read a stream, including how many events are waiting to be delivered or acknowledged. Scope: {@code ssf:read}. */
    public SsfStreamsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/ssf/streams/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/ssf/streams/{id}} - Change a stream: its events, endpoint, authorization header, description, or status (`enabled`, `paused`, `disabled`). A paused stream stops receiving new events. Scope: {@code ssf:write}. */
    public JsonNode update(String id, SsfStreamsUpdateBody body) {
        return t.request("PATCH", "/v1/ssf/streams/" + enc(id), null, body, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code DELETE /v1/ssf/streams/{id}} - Delete a stream and its queued events. Scope: {@code ssf:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/ssf/streams/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/ssf/streams/{id}/verify} - Queue an SSF verification event carrying `state`, so a receiver can confirm the stream works end to end. Scope: {@code ssf:write}. */
    public JsonNode verify(String id, SsfStreamsVerifyBody body) {
        return verify(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode verify(String id, SsfStreamsVerifyBody body, String idempotencyKey) {
        return t.request("POST", "/v1/ssf/streams/" + enc(id) + "/verify", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/ssf/streams/{id}/status} - The stream's delivery status: enabled, paused or disabled (SSF stream status). Scope: {@code ssf:read}. */
    public SsfStreamsGetStatusResponse getStatus(String id) {
        return t.request("GET", "/v1/ssf/streams/" + enc(id) + "/status", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/ssf/streams/{id}/status} - Set the stream's delivery status to enabled, paused or disabled. A paused stream collects nothing. Scope: {@code ssf:write}. */
    public SsfStreamsGetStatusResponse updateStatus(String id, SsfStreamsUpdateStatusBody body) {
        return updateStatus(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public SsfStreamsGetStatusResponse updateStatus(String id, SsfStreamsUpdateStatusBody body, String idempotencyKey) {
        return t.request("POST", "/v1/ssf/streams/" + enc(id) + "/status", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/ssf/streams/{id}/events} - The stream's recent events with delivery status, attempts and the last error (the dashboard activity view). Scope: {@code ssf:read}. */
    public SsfStreamsEventsResponse events(String id) {
        return events(id, null, null);
    }

    /** As above, with every optional parameter. */
    public SsfStreamsEventsResponse events(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/ssf/streams/" + enc(id) + "/events", q, null, null, false, new TypeReference<>() {
        });
    }
}
