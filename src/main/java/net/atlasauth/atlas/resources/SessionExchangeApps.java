package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSessionExchangeAppBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.Session;
import net.atlasauth.atlas.models.SessionExchangeApp;
import net.atlasauth.atlas.models.UpdateSessionExchangeAppBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Session-exchange apps ({@code /v1/session_exchange_apps}). */
public final class SessionExchangeApps {

    private final Transport t;

    public SessionExchangeApps(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/session_exchange_apps} — List registered session-exchange apps (downstream apps that exchange an Atlas session for their own) and their back-channel logout endpoints. */
    public ListPage<SessionExchangeApp> list() {
        return t.get("/v1/session_exchange_apps", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/session_exchange_apps} — Register a session-exchange app and its backchannel_logout_uri. */
    public SessionExchangeApp create(CreateSessionExchangeAppBody body) {
        return t.post("/v1/session_exchange_apps", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public SessionExchangeApp create(CreateSessionExchangeAppBody body, String idempotencyKey) {
        return t.post("/v1/session_exchange_apps", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/session_exchange_apps/{id}} — Fetch one session-exchange app. */
    public SessionExchangeApp get(String id) {
        return t.get("/v1/session_exchange_apps/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/session_exchange_apps/{id}} — Update a session-exchange app's name, backchannel_logout_uri or audience. */
    public SessionExchangeApp update(String id, UpdateSessionExchangeAppBody body) {
        return t.patch("/v1/session_exchange_apps/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/session_exchange_apps/{id}} — Delete a session-exchange app; its queued back-channel logout deliveries cascade with it. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/session_exchange_apps/" + enc(id), new TypeReference<>() {
        });
    }
}
