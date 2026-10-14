package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.MintedSession;
import net.atlasauth.atlas.models.Session;
import net.atlasauth.atlas.models.SessionTemplateToken;
import net.atlasauth.atlas.models.SessionTouch;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/sessions} resource. */
public final class Sessions {

    private final Transport t;

    public Sessions(Transport t) {
        this.t = t;
    }

    /**
     * {@code POST /v1/sessions} — mint a session for a user without the sign-in
     * flow. Returns the bearer jwt + refresh token in-body. Refused for a banned user.
     */
    public MintedSession create(String userId) {
        return create(userId, null);
    }

    /** @param actorSub optional impersonation actor — records who is acting as the user. */
    public MintedSession create(String userId, String actorSub) {
        Params body = Params.of().set("user_id", userId);
        if (actorSub != null) {
            body.set("actor", Map.of("sub", actorSub));
        }
        return t.post("/v1/sessions", body.map(), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/sessions?user_id=} — sessions are always scoped to one user. */
    public ListPage<Session> list(String userId) {
        Map<String, Object> q = Params.of("user_id", userId).map();
        return t.get("/v1/sessions", q, new TypeReference<>() {
        });
    }

    public Session get(String id) {
        return t.get("/v1/sessions/" + enc(id), new TypeReference<>() {
        });
    }

    public JsonNode revoke(String id) {
        return t.post("/v1/sessions/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/sessions/{id}/touch} — keep a session alive from your backend:
     * slides its inactivity window as if the user had just loaded a page, never past
     * the absolute expiry. {@code extended} is false when a touch within the last minute
     * already moved it, so calling it on every request is safe. An ended session is a
     * 409 ({@code SESSION_REVOKED}, {@code SESSION_IDLE_EXPIRED} or {@code SESSION_EXPIRED}).
     */
    public SessionTouch touch(String id) {
        return t.post("/v1/sessions/" + enc(id) + "/touch", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/sessions/{id}/tokens/{template}} — Mint a named JWT-template token for a session (Clerk SessionApi.getToken). */
    public SessionTemplateToken createToken(String id, String template) {
        return t.post("/v1/sessions/" + enc(id) + "/tokens/" + enc(template), null, new TypeReference<>() {
        });
    }
}
