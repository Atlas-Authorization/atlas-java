package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.FgaAuthorizationModel;
import net.atlasauth.atlas.models.FgaAuthorizationModelInput;
import net.atlasauth.atlas.models.FgaBatchCheckBody;
import net.atlasauth.atlas.models.FgaBatchCheckResult;
import net.atlasauth.atlas.models.FgaCheckBody;
import net.atlasauth.atlas.models.FgaCheckResult;
import net.atlasauth.atlas.models.FgaExpandResult;
import net.atlasauth.atlas.models.FgaListObjectsBody;
import net.atlasauth.atlas.models.FgaListObjectsResult;
import net.atlasauth.atlas.models.FgaStore;
import net.atlasauth.atlas.models.FgaTuple;
import net.atlasauth.atlas.models.FgaWriteBody;
import net.atlasauth.atlas.models.FgaWriteResult;
import net.atlasauth.atlas.models.ListPage;

import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.models.FgaListUsersBody;
import net.atlasauth.atlas.models.FgaListUsersResponse;
import net.atlasauth.atlas.models.FgaReadChangesResponse;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The {@code /v1/fga} resource — fine-grained relationship-based authorization
 * (Zanzibar / OpenFGA). A tenant owns stores; each store holds immutable,
 * versioned authorization models and the relationship tuples the engine
 * resolves. Every store is scoped to the calling instance.
 */
public final class Fga {

    private final Transport t;
    private final Stores stores;
    private final Models models;

    public Fga(Transport t) {
        this.t = t;
        this.stores = new Stores(t);
        this.models = new Models(t);
    }

    public Stores stores() {
        return stores;
    }

    public Models models() {
        return models;
    }

    // ---- store-scoped operations ------------------------------------------

    /** Write and/or delete tuples in one atomic call. */
    public FgaWriteResult write(String storeId, FgaWriteBody body) {
        return write(storeId, body, null);
    }

    public FgaWriteResult write(String storeId, FgaWriteBody body, String idempotencyKey) {
        return t.post("/v1/fga/stores/" + enc(storeId) + "/write", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** Query stored tuples by any of user / relation / object. */
    public ListPage<FgaTuple> read(String storeId, String user, String relation, String object) {
        Map<String, Object> body = Params.of()
                .set("user", user)
                .set("relation", relation)
                .set("object", object)
                .map();
        return t.post("/v1/fga/stores/" + enc(storeId) + "/read", body, new TypeReference<>() {
        });
    }

    /** Resolve a single access question against the model. */
    public FgaCheckResult check(String storeId, FgaCheckBody body) {
        return t.post("/v1/fga/stores/" + enc(storeId) + "/check", body, new TypeReference<>() {
        });
    }

    /** Resolve many access questions in one round trip (list-authorization UIs). */
    public FgaBatchCheckResult batchCheck(String storeId, FgaBatchCheckBody body) {
        return t.post("/v1/fga/stores/" + enc(storeId) + "/batch-check", body, new TypeReference<>() {
        });
    }

    /** List the objects of a type a user has a relation to. */
    public FgaListObjectsResult listObjects(String storeId, FgaListObjectsBody body) {
        return t.post("/v1/fga/stores/" + enc(storeId) + "/list-objects", body, new TypeReference<>() {
        });
    }

    /** Expand the full userset tree for an object#relation. */
    public FgaExpandResult expand(String storeId, String object, String relation, String authorizationModelId) {
        Map<String, Object> body = Params.of()
                .set("object", object)
                .set("relation", relation)
                .set("authorization_model_id", authorizationModelId)
                .map();
        return t.post("/v1/fga/stores/" + enc(storeId) + "/expand", body, new TypeReference<>() {
        });
    }

    /**
     * Bind a default store so an app that uses one store (the common case) can
     * call {@code fga.store(id).check(...)} instead of threading the store id
     * through every call. Returns the same operations with {@code storeId}
     * pre-applied.
     */
    public BoundStore store(String storeId) {
        return new BoundStore(this, storeId);
    }

    // ---- nested sub-resources ---------------------------------------------

    public static final class Stores {
        private final Transport t;

        Stores(Transport t) {
            this.t = t;
        }

        public ListPage<FgaStore> list() {
            return t.get("/v1/fga/stores", new TypeReference<>() {
            });
        }

        public FgaStore create(String name) {
            return create(name, null);
        }

        public FgaStore create(String name, String idempotencyKey) {
            Map<String, Object> body = Params.of("name", name).map();
            return t.post("/v1/fga/stores", body, idempotencyKey, new TypeReference<>() {
            });
        }

        public FgaStore get(String id) {
            return t.get("/v1/fga/stores/" + enc(id), new TypeReference<>() {
            });
        }

        public DeletedObject delete(String id) {
            return t.delete("/v1/fga/stores/" + enc(id), new TypeReference<>() {
            });
        }
    }

    public static final class Models {
        private final Transport t;

        Models(Transport t) {
            this.t = t;
        }

        public ListPage<FgaAuthorizationModel> list(String storeId) {
            return t.get("/v1/fga/stores/" + enc(storeId) + "/authorization-models", new TypeReference<>() {
            });
        }

        public FgaAuthorizationModel create(String storeId, FgaAuthorizationModelInput body) {
            return create(storeId, body, null);
        }

        public FgaAuthorizationModel create(String storeId, FgaAuthorizationModelInput body, String idempotencyKey) {
            return t.post("/v1/fga/stores/" + enc(storeId) + "/authorization-models", body, idempotencyKey,
                    new TypeReference<>() {
                    });
        }

        public FgaAuthorizationModel get(String storeId, String modelId) {
            return t.get("/v1/fga/stores/" + enc(storeId) + "/authorization-models/" + enc(modelId),
                    new TypeReference<>() {
                    });
        }
    }

    /** A store-scoped view of the FGA operations, with {@code storeId} pre-applied. */
    public static final class BoundStore {
        private final Fga fga;
        private final String storeId;
        private final BoundModels models;

        BoundStore(Fga fga, String storeId) {
            this.fga = fga;
            this.storeId = storeId;
            this.models = new BoundModels(fga, storeId);
        }

        public FgaWriteResult write(FgaWriteBody body) {
            return fga.write(storeId, body);
        }

        public FgaWriteResult write(FgaWriteBody body, String idempotencyKey) {
            return fga.write(storeId, body, idempotencyKey);
        }

        public ListPage<FgaTuple> read(String user, String relation, String object) {
            return fga.read(storeId, user, relation, object);
        }

        public FgaCheckResult check(FgaCheckBody body) {
            return fga.check(storeId, body);
        }

        public FgaBatchCheckResult batchCheck(FgaBatchCheckBody body) {
            return fga.batchCheck(storeId, body);
        }

        public FgaListObjectsResult listObjects(FgaListObjectsBody body) {
            return fga.listObjects(storeId, body);
        }

        public FgaExpandResult expand(String object, String relation, String authorizationModelId) {
            return fga.expand(storeId, object, relation, authorizationModelId);
        }

        public BoundModels models() {
            return models;
        }
    }

    /** A store-scoped view of the model operations. */
    public static final class BoundModels {
        private final Fga fga;
        private final String storeId;

        BoundModels(Fga fga, String storeId) {
            this.fga = fga;
            this.storeId = storeId;
        }

        public ListPage<FgaAuthorizationModel> list() {
            return fga.models.list(storeId);
        }

        public FgaAuthorizationModel create(FgaAuthorizationModelInput body) {
            return fga.models.create(storeId, body);
        }

        public FgaAuthorizationModel create(FgaAuthorizationModelInput body, String idempotencyKey) {
            return fga.models.create(storeId, body, idempotencyKey);
        }

        public FgaAuthorizationModel get(String modelId) {
            return fga.models.get(storeId, modelId);
        }
    }

    /** {@code POST /v1/fga/stores/{id}/list-users} - List the users that have a relation on an object, filtered to one user type (or to `type#relation` usersets via `user_filters[0].relation`). Exact for wildcards (`type:*` plus `excluded_users` under `but not`), nested groups and contextual tuples; tuple conditions are evaluated against `context`. Pages with `page_size` / `continuation_token`; `consistency` / `consistency_token` behave as on check. Scope: {@code fga:read}. */
    public FgaListUsersResponse listUsers(String id, FgaListUsersBody body) {
        return listUsers(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public FgaListUsersResponse listUsers(String id, FgaListUsersBody body, String idempotencyKey) {
        return t.request("POST", "/v1/fga/stores/" + enc(id) + "/list-users", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/fga/stores/{id}/changes} - Read the store's tuple changelog: every tuple write and delete in commit order, resumable with `continuation_token` (returned even on the last page - pass it later to receive only newer changes). Filter by object `type` and `start_time`. Scope: {@code fga:read}. */
    public FgaReadChangesResponse readChanges(String id) {
        return readChanges(id, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public FgaReadChangesResponse readChanges(String id, String type, Long pageSize, String continuationToken, String startTime, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("type", type).set("page_size", pageSize).set("continuation_token", continuationToken).set("start_time", startTime).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/fga/stores/" + enc(id) + "/changes", q, null, null, false, new TypeReference<>() {
        });
    }
}
