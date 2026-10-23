package net.atlasauth.atlas.resources;

import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateScimTokenBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ScimToken;
import net.atlasauth.atlas.models.ScimTokenWithSecret;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/scim_tokens} resource (inbound SCIM bearer tokens). */
public final class ScimTokens {

    private final Transport t;

    public ScimTokens(Transport t) {
        this.t = t;
    }

    public ListPage<ScimToken> list() {
        return t.get("/v1/scim_tokens", new TypeReference<>() {
        });
    }

    public ScimTokenWithSecret create(CreateScimTokenBody body) {
        return create(body, null);
    }

    public ScimTokenWithSecret create(CreateScimTokenBody body, String idempotencyKey) {
        return t.post("/v1/scim_tokens", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public JsonNode revoke(String id) {
        return t.post("/v1/scim_tokens/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }

    /**
     * {@code PATCH /v1/scim_tokens/{id}} — change {@code name}, {@code scopes} ({@code ["read"]} =
     * read-only) or {@code role_mappings} ({@code [{value, role}]}). The secret is unchanged.
     */
    public JsonNode update(String id, Map<String, Object> body) {
        return t.patch("/v1/scim_tokens/" + enc(id), body, new TypeReference<>() {
        });
    }
}
