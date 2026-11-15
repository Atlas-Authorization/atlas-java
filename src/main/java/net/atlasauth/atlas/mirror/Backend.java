package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The few Backend API calls the mirrors make, as an interface so a mirror can be driven by the real client
 * ({@link #of(AtlasClient)}) or by a fake in a test. Bodies and answers are plain JSON.
 */
public interface Backend {

    /** {@code POST /v1/api_keys}. */
    JsonNode createApiKey(Map<String, Object> body, String idempotencyKey);

    /** {@code DELETE /v1/api_keys/{id}}. */
    void deleteApiKey(String id);

    /** {@code GET /v1/api_keys/revoked}: one page of the revocation feed, from {@code since} (null = the start). */
    JsonNode listRevokedApiKeys(String since, int limit);

    /** {@code POST /v1/api_keys/verify}; the default refuses, so a mirror without it never asks Atlas. */
    default JsonNode verifyApiKey(String secret, String clientIp) {
        throw new UnsupportedOperationException("this Backend cannot verify API keys at Atlas");
    }

    /** {@code GET /v1/api_keys/{id}}. */
    default JsonNode getApiKey(String id) {
        throw new UnsupportedOperationException("this Backend cannot read API keys");
    }

    /** {@code POST /v1/api_keys/{id}/rotate}. */
    default JsonNode rotateApiKey(String id, Map<String, Object> body, String idempotencyKey) {
        throw new UnsupportedOperationException("this Backend cannot rotate API keys");
    }

    /** {@code GET /v1/resource_types}: {@code {data: [{key, inherit, roles}]}}. */
    default JsonNode listResourceTypes() {
        throw new UnsupportedOperationException("this Backend cannot list resource types");
    }

    /** {@code GET /v1/resources}: one page, {@code {data, has_more, next_cursor}}. */
    default JsonNode listResources(String startingAfter, int limit) {
        throw new UnsupportedOperationException("this Backend cannot list resources");
    }

    /** {@code GET /v1/resource_role_assignments/changes}: one page of the assignment change feed. */
    default JsonNode listResourceRoleAssignmentChanges(String after, int limit) {
        throw new UnsupportedOperationException("this Backend cannot read the assignment change feed");
    }

    /** {@code GET /v1/sessions/revoked}: one page of revoked session ids. */
    default JsonNode listRevokedSessions(Long since, String cursor, int limit) {
        throw new UnsupportedOperationException("this Backend cannot list revoked sessions");
    }

    /** {@code GET /v1/sessions/revocations/status-list}: {@code {token}}, the signed list (null window = the server default). */
    default JsonNode getSessionStatusList(Integer windowSeconds) {
        throw new UnsupportedOperationException("this Backend cannot read the session status list");
    }

    /** The real implementation over an {@link AtlasClient}'s transport (secret key, retries and telemetry included). */
    static Backend of(AtlasClient client) {
        Transport t = client.transport();
        TypeReference<JsonNode> json = new TypeReference<>() {
        };
        return new Backend() {
            @Override
            public JsonNode createApiKey(Map<String, Object> body, String idempotencyKey) {
                return t.request("POST", "/v1/api_keys", null, body, idempotencyKey, false, json);
            }

            @Override
            public void deleteApiKey(String id) {
                t.delete("/v1/api_keys/" + enc(id), json);
            }

            @Override
            public JsonNode listRevokedApiKeys(String since, int limit) {
                return t.get("/v1/api_keys/revoked", Params.of("since", since).set("limit", (long) limit).map(), json);
            }

            @Override
            public JsonNode verifyApiKey(String secret, String clientIp) {
                return t.post("/v1/api_keys/verify", Params.of("secret", secret).set("client_ip", clientIp).map(), json);
            }

            @Override
            public JsonNode getApiKey(String id) {
                return t.get("/v1/api_keys/" + enc(id), json);
            }

            @Override
            public JsonNode rotateApiKey(String id, Map<String, Object> body, String idempotencyKey) {
                return t.request("POST", "/v1/api_keys/" + enc(id) + "/rotate", null, body, idempotencyKey, false, json);
            }

            @Override
            public JsonNode listResourceTypes() {
                return t.get("/v1/resource_types", json);
            }

            @Override
            public JsonNode listResources(String startingAfter, int limit) {
                return t.get("/v1/resources", Params.of("starting_after", startingAfter).set("limit", (long) limit).map(), json);
            }

            @Override
            public JsonNode listResourceRoleAssignmentChanges(String after, int limit) {
                return t.get("/v1/resource_role_assignments/changes", Params.of("after", after).set("limit", (long) limit).map(), json);
            }

            @Override
            public JsonNode getSessionStatusList(Integer windowSeconds) {
                return t.get("/v1/sessions/revocations/status-list", Params.of("window_seconds", windowSeconds == null ? null : (long) windowSeconds).map(), json);
            }

            @Override
            public JsonNode listRevokedSessions(Long since, String cursor, int limit) {
                return t.get("/v1/sessions/revoked", Params.of("since", since).set("cursor", cursor).set("limit", (long) limit).map(), json);
            }
        };
    }
}
