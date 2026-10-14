package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ApiKey;
import net.atlasauth.atlas.models.ApiKeyToken;
import net.atlasauth.atlas.models.ApiKeyVerification;
import net.atlasauth.atlas.models.ApiKeyWithSecret;
import net.atlasauth.atlas.models.CreateApiKeyBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.MintApiKeyTokenBody;
import net.atlasauth.atlas.models.RevokedApiKey;
import net.atlasauth.atlas.models.UpdateApiKeyBody;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/api_keys} resource — end-user API keys a tenant mints and verifies. */
public final class ApiKeys {

    private final Transport t;

    public ApiKeys(Transport t) {
        this.t = t;
    }

    public ListPage<ApiKey> list() {
        return list(null, null);
    }

    /** Optionally narrowed to one subject. */
    public ListPage<ApiKey> list(String subjectType, String subjectId) {
        Map<String, Object> q = Params.of()
                .set("subject_type", subjectType)
                .set("subject_id", subjectId)
                .map();
        return t.get("/v1/api_keys", q, new TypeReference<>() {
        });
    }

    public ApiKeyWithSecret create(CreateApiKeyBody body) {
        return create(body, null);
    }

    /** Mint a key. The secret is returned once, here. */
    public ApiKeyWithSecret create(CreateApiKeyBody body, String idempotencyKey) {
        return t.post("/v1/api_keys", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** Check a presented secret. Rate-limited — it is an online credential check. */
    public ApiKeyVerification verify(String secret) {
        Map<String, Object> body = Params.of("secret", secret).map();
        return t.post("/v1/api_keys/verify", body, new TypeReference<>() {
        });
    }

    public ApiKey get(String id) {
        return t.get("/v1/api_keys/" + enc(id), new TypeReference<>() {
        });
    }

    public ApiKey update(String id, UpdateApiKeyBody body) {
        return t.patch("/v1/api_keys/" + enc(id), body, new TypeReference<>() {
        });
    }

    /**
     * Revoke a key. It stays queryable but never authenticates again. Returns
     * {@code {object,id,revoked:true}}.
     */
    public JsonNode delete(String id) {
        return t.delete("/v1/api_keys/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/api_keys/token} — Mint a short-lived, JWKS-signed access JWT (token_use:api_key, jti) whose exp is clamped to the smallest of the request, the key expiry and the org ceiling; scopes + constraints are embedded so a resource verifies it OFFLINE against the instance JWKS. */
    public ApiKeyToken mintToken(MintApiKeyTokenBody body) {
        return t.post("/v1/api_keys/token", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public ApiKeyToken mintToken(MintApiKeyTokenBody body, String idempotencyKey) {
        return t.post("/v1/api_keys/token", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public ListPage<RevokedApiKey> listRevoked() {
        return listRevoked(null, null);
    }

    /** {@code GET /v1/api_keys/revoked} — Revocation feed: the keys revoked since a cursor (since=&lt;iso|cursor&gt;), so a resource can deny-list revoked key ids while their short-lived delegated tokens are still live. */
    public ListPage<RevokedApiKey> listRevoked(String since, Long limit) {
        Map<String, Object> q = Params.of().set("since", since).set("limit", limit).map();
        return t.get("/v1/api_keys/revoked", q, new TypeReference<>() {
        });
    }
}
