package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ListenCreateSessionBody;
import net.atlasauth.atlas.models.ListenCreateSessionResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/listen} resource. */
public final class Listen {

    private final Transport t;

    public Listen(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/listen/sessions} - Start a local webhook forwarding session (what `atlas listen` uses). Returns a session id, a signing secret for that session only (it is unrelated to any registered endpoint's secret) and the stream URL. The session needs no storage: it expires at `expires_at` (default 1 hour, `ttl_seconds` 60 to 43200). Scope: {@code webhooks:write}. */
    public ListenCreateSessionResponse createSession(ListenCreateSessionBody body) {
        return createSession(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ListenCreateSessionResponse createSession(ListenCreateSessionBody body, String idempotencyKey) {
        return t.request("POST", "/v1/listen/sessions", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/listen/sessions/{id}/stream} - Server-Sent Events stream of the instance's new events for a listen session. Each `webhook` event carries `&#123;id, type, headers, body&#125;`: the headers (`atlas-id`, `atlas-timestamp`, `atlas-signature`) and the exact JSON body of a would-be delivery, signed with the session secret. Starts at "now" unless resumed with `Last-Event-ID` or `after`. Query: `events` (comma-separated types or patterns), `max_events` and `max_wait_seconds` (end the stream early, for scripts and CI). A `: keep-alive` comment is sent every 15 seconds; an `expired` event ends the stream at the session expiry. 404 for an unknown session id, 400 once expired. Scope: {@code webhooks:read}. */
    public String streamSession(String id) {
        return streamSession(id, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public String streamSession(String id, String events, String after, Long maxEvents, Long pollMs, Long maxWaitSeconds) {
        Map<String, Object> q = Params.of().set("events", events).set("after", after).set("max_events", maxEvents).set("poll_ms", pollMs).set("max_wait_seconds", maxWaitSeconds).map();
        return t.getRaw("/v1/listen/sessions/" + enc(id) + "/stream", q);
    }
}
