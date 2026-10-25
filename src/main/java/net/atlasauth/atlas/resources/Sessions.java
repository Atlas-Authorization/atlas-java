package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IntegrationToken;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.MintedSession;
import net.atlasauth.atlas.models.Session;
import net.atlasauth.atlas.models.SessionTemplateToken;
import net.atlasauth.atlas.models.SessionTouch;

import java.util.LinkedHashMap;
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

    /**
     * {@code POST /v1/sessions/{id}/integration_tokens/{integration}} — Mint an identity-integration token for
     * this session (the server-side form of {@code getToken({ for })}): a preset such as {@code chatly}, or a
     * custom integration id. The token lives at most 120 seconds.
     */
    public IntegrationToken getIntegrationToken(String id, String integration) {
        return t.post("/v1/sessions/" + enc(id) + "/integration_tokens/" + enc(integration), null, new TypeReference<>() {
        });
    }

    /**
     * {@link #getIntegrationToken(String, String)} for a per-call audience. {@code audience} is the full audience,
     * {@code audienceParams} carries the integration's own parameter (a site id, a channel, an organization id; the
     * parameter named in its {@code per_call_audience.param}). Either may be null. The token's {@code aud} is that
     * audience when the integration permits it; a value it does not permit is refused (403).
     */
    public IntegrationToken getIntegrationToken(String id, String integration, String audience, Map<String, String> audienceParams) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (audienceParams != null) query.putAll(audienceParams);
        if (audience != null && !audience.isEmpty()) query.put("audience", audience);
        return t.postWithQuery("/v1/sessions/" + enc(id) + "/integration_tokens/" + enc(integration), query, null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/sessions/{id}/tokens/{template}} — Mint a named JWT-template token for a session (Clerk SessionApi.getToken). */
    public SessionTemplateToken createToken(String id, String template) {
        return t.post("/v1/sessions/" + enc(id) + "/tokens/" + enc(template), null, new TypeReference<>() {
        });
    }

    /**
     * {@code PUT /v1/sessions/{id}/custom_data} — replace the session's custom data
     * ({@code null} clears it). At most 2 KB; only an active session can be changed. Not
     * for secrets: the signed-in user can read it. Sends the {@code session.updated} webhook.
     */
    public Session setCustomData(String id, Map<String, Object> customData) {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("custom_data", customData);
        return t.put("/v1/sessions/" + enc(id) + "/custom_data", body, new TypeReference<>() {
        });
    }

    /**
     * {@code PATCH /v1/sessions/{id}/custom_data} — merge keys into the session's custom
     * data; a key mapped to {@code null} is removed.
     */
    public Session updateCustomData(String id, Map<String, Object> patch) {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("custom_data", patch);
        return t.patch("/v1/sessions/" + enc(id) + "/custom_data", body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/sessions} with custom per-session data (a JSON object, at most 2 KB). */
    public MintedSession create(String userId, String actorSub, Map<String, Object> customData) {
        Params body = Params.of().set("user_id", userId).set("custom_data", customData);
        if (actorSub != null) {
            body.set("actor", Map.of("sub", actorSub));
        }
        return t.post("/v1/sessions", body.map(), new TypeReference<>() {
        });
    }
}
