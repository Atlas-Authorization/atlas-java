package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ResourcesAccessibleBody;
import net.atlasauth.atlas.models.ResourcesCheckBatchBody;
import net.atlasauth.atlas.models.ResourcesCheckBatchResponse;
import net.atlasauth.atlas.models.ResourcesCheckBody;
import net.atlasauth.atlas.models.ResourcesCheckResponse;
import net.atlasauth.atlas.models.ResourcesListResponse;
import net.atlasauth.atlas.models.ResourcesListResponseDataItem;
import net.atlasauth.atlas.models.ResourcesSyncBody;
import net.atlasauth.atlas.models.ResourcesUpsertBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/resources} resource. */
public final class Resources {

    private final Transport t;

    public Resources(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/resources} - List resources, optionally filtered by `type` or `parent_id`. Scope: {@code resources:read}. */
    public ResourcesListResponse list() {
        return list(null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public ResourcesListResponse list(String type, String parentId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("type", type).set("parent_id", parentId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/resources", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/resources} - Create or update one resource by `type` + `external_id` (your own id for it). `parent` is `&#123;id&#125;` or `&#123;type, external_id&#125;` of an existing resource, or null; it must match the type's `parent_type`. Scope: {@code resources:write}. */
    public ResourcesListResponseDataItem upsert(ResourcesUpsertBody body) {
        return t.request("PUT", "/v1/resources", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/resources/sync} - Discovery sync: upsert up to 500 resources in order (list parents before children). Idempotent; call it whenever your app creates or moves resources. Scope: {@code resources:write}. */
    public ResourcesListResponse sync(ResourcesSyncBody body) {
        return sync(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ResourcesListResponse sync(ResourcesSyncBody body, String idempotencyKey) {
        return t.request("POST", "/v1/resources/sync", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/resources/{id}} - Read one resource, with its type and parent. Scope: {@code resources:read}. */
    public ResourcesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/resources/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/resources/{id}} - Delete a resource and its role assignments. Its children are detached to the root. Scope: {@code resources:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/resources/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/resource_checks} - Does `subject` hold `permission` on `resource`? A user subject also holds the roles of the organizations they belong to, plus any `groups` ids you pass. The response lists the grants that allow it. Scope: {@code resources:check}. */
    public ResourcesCheckResponse check(ResourcesCheckBody body) {
        return check(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ResourcesCheckResponse check(ResourcesCheckBody body, String idempotencyKey) {
        return t.request("POST", "/v1/resource_checks", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/resource_checks/batch} - Up to 100 resource checks in one call. Scope: {@code resources:check}. */
    public ResourcesCheckBatchResponse checkBatch(ResourcesCheckBatchBody body) {
        return checkBatch(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ResourcesCheckBatchResponse checkBatch(ResourcesCheckBatchBody body, String idempotencyKey) {
        return t.request("POST", "/v1/resource_checks/batch", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/resource_checks/list} - List the resources of an optional `type` on which `subject` holds `permission`, paged with `limit` and `starting_after` (the response carries `has_more` and `next_cursor`). Scope: {@code resources:check}. */
    public ResourcesListResponse accessible(ResourcesAccessibleBody body) {
        return accessible(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ResourcesListResponse accessible(ResourcesAccessibleBody body, String idempotencyKey) {
        return t.request("POST", "/v1/resource_checks/list", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** The {@code types} sub-resource. */
    public ResourcesTypes types() {
        return new ResourcesTypes(t);
    }

    /** The {@code assignments} sub-resource. */
    public ResourcesAssignments assignments() {
        return new ResourcesAssignments(t);
    }
}
