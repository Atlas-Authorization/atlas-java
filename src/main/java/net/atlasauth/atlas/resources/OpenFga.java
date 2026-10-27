package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OpenFgaAuthorizationModelBody;
import net.atlasauth.atlas.models.OpenFgaAuthorizationModelList;
import net.atlasauth.atlas.models.OpenFgaBatchCheckBody;
import net.atlasauth.atlas.models.OpenFgaBatchCheckResult;
import net.atlasauth.atlas.models.OpenFgaCheckBody;
import net.atlasauth.atlas.models.OpenFgaCheckResult;
import net.atlasauth.atlas.models.OpenFgaExpandBody;
import net.atlasauth.atlas.models.OpenFgaExpandResult;
import net.atlasauth.atlas.models.OpenFgaListObjectsBody;
import net.atlasauth.atlas.models.OpenFgaListObjectsResult;
import net.atlasauth.atlas.models.OpenFgaReadBody;
import net.atlasauth.atlas.models.OpenFgaReadModelResult;
import net.atlasauth.atlas.models.OpenFgaReadResult;
import net.atlasauth.atlas.models.OpenFgaStore;
import net.atlasauth.atlas.models.OpenFgaStoreList;
import net.atlasauth.atlas.models.OpenFgaWriteBody;
import net.atlasauth.atlas.models.OpenFgaWriteModelResult;

import java.util.List;
import java.util.Map;
import net.atlasauth.atlas.models.OpenFgaListUsersBody;
import net.atlasauth.atlas.models.OpenFgaListUsersResponse;
import net.atlasauth.atlas.models.OpenFgaReadChangesResponse;

import static net.atlasauth.atlas.http.Transport.enc;

/** The OpenFGA-compatible authorization API ({@code /v1/openfga}). */
public final class OpenFga {

    private final Transport t;

    public OpenFga(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/openfga/stores} — List the instance's OpenFGA (ReBAC) authorization stores. */
    public OpenFgaStoreList listStores() {
        return t.get("/v1/openfga/stores", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores} — Create an OpenFGA authorization store. */
    public OpenFgaStore createStore(String name) {
        Map<String, Object> payload = Params.of().set("name", name).map();
        return t.post("/v1/openfga/stores", payload, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/openfga/stores/{store}} — Get one OpenFGA store (OpenFGA-wire GetStore). */
    public OpenFgaStore getStore(String store) {
        return t.get("/v1/openfga/stores/" + enc(store), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/authorization-models} — Write a new authorization model to a store. */
    public OpenFgaWriteModelResult writeAuthorizationModel(String store, OpenFgaAuthorizationModelBody body) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/authorization-models", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OpenFgaWriteModelResult writeAuthorizationModel(String store, OpenFgaAuthorizationModelBody body, String idempotencyKey) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/authorization-models", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/openfga/stores/{store}/authorization-models} — List the authorization models in a store. */
    public OpenFgaAuthorizationModelList listAuthorizationModels(String store) {
        return t.get("/v1/openfga/stores/" + enc(store) + "/authorization-models", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/openfga/stores/{store}/authorization-models/{mid}} — Read one authorization model (OpenFGA-wire ReadAuthorizationModel). */
    public OpenFgaReadModelResult readAuthorizationModel(String store, String mid) {
        return t.get("/v1/openfga/stores/" + enc(store) + "/authorization-models/" + enc(mid), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/check} — Check whether a user has a relation on an object (OpenFGA-wire check). */
    public OpenFgaCheckResult check(String store, OpenFgaCheckBody body) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/check", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OpenFgaCheckResult check(String store, OpenFgaCheckBody body, String idempotencyKey) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/check", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/batch-check} — Check up to 100 tuples in one request, keyed by correlation id (OpenFGA-wire BatchCheck). */
    public OpenFgaBatchCheckResult batchCheck(String store, OpenFgaBatchCheckBody body) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/batch-check", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OpenFgaBatchCheckResult batchCheck(String store, OpenFgaBatchCheckBody body, String idempotencyKey) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/batch-check", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/write} — Write or delete relationship tuples in a store (OpenFGA-wire write). */
    public JsonNode write(String store, OpenFgaWriteBody body) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/write", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode write(String store, OpenFgaWriteBody body, String idempotencyKey) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/write", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/read} — Read relationship tuples, optionally filtered by a tuple key (OpenFGA-wire Read). */
    public OpenFgaReadResult read(String store, OpenFgaReadBody body) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/read", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OpenFgaReadResult read(String store, OpenFgaReadBody body, String idempotencyKey) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/read", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/list-objects} — List the objects of a type a user has a relation on (OpenFGA-wire ListObjects). */
    public OpenFgaListObjectsResult listObjects(String store, OpenFgaListObjectsBody body) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/list-objects", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OpenFgaListObjectsResult listObjects(String store, OpenFgaListObjectsBody body, String idempotencyKey) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/list-objects", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/expand} — Expand the userset tree for an (object, relation) (OpenFGA-wire Expand). */
    public OpenFgaExpandResult expand(String store, OpenFgaExpandBody body) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/expand", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OpenFgaExpandResult expand(String store, OpenFgaExpandBody body, String idempotencyKey) {
        return t.post("/v1/openfga/stores/" + enc(store) + "/expand", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/openfga/stores/{store}/list-users} - List the users that have a relation on an object (OpenFGA-wire ListUsers): concrete users, usersets (`user_filters[0].relation`), and the `type:*` wildcard with `excluded_users`. Honours contextual tuples and tuple conditions; pages with `page_size` / `continuation_token`. Scope: {@code fga:read}. */
    public OpenFgaListUsersResponse listUsers(String store, OpenFgaListUsersBody body) {
        return listUsers(store, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public OpenFgaListUsersResponse listUsers(String store, OpenFgaListUsersBody body, String idempotencyKey) {
        return t.request("POST", "/v1/openfga/stores/" + enc(store) + "/list-users", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/openfga/stores/{store}/changes} - Read the tuple changelog (OpenFGA-wire ReadChanges): every tuple write and delete in order, resumable with `continuation_token` (the token is returned even on the last page - pass it later to receive only newer changes). Scope: {@code fga:read}. */
    public OpenFgaReadChangesResponse readChanges(String store) {
        return readChanges(store, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public OpenFgaReadChangesResponse readChanges(String store, String type, Long pageSize, String continuationToken, String startTime) {
        Map<String, Object> q = Params.of().set("type", type).set("page_size", pageSize).set("continuation_token", continuationToken).set("start_time", startTime).map();
        return t.request("GET", "/v1/openfga/stores/" + enc(store) + "/changes", q, null, null, false, new TypeReference<>() {
        });
    }
}
