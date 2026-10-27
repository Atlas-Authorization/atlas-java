package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ResourcesTypesCreateBody;
import net.atlasauth.atlas.models.ResourcesTypesListResponse;
import net.atlasauth.atlas.models.ResourcesTypesListResponseDataItem;
import net.atlasauth.atlas.models.ResourcesTypesUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/resource_types} resource. */
public final class ResourcesTypes {

    private final Transport t;

    public ResourcesTypes(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/resource_types} - List the resource types (roles, permissions, parent type). Scope: {@code resources:read}. */
    public ResourcesTypesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public ResourcesTypesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/resource_types", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/resource_types} - Define a resource type: a `key`, its `roles` (each a set of permissions, optionally `includes` other roles), an optional `parent_type` resources nest under, and `inherit` (default true) controlling whether assignments on a parent flow down. Scope: {@code resources:write}. */
    public ResourcesTypesListResponseDataItem create(ResourcesTypesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ResourcesTypesListResponseDataItem create(ResourcesTypesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/resource_types", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/resource_types/{key}} - Read one resource type. Scope: {@code resources:read}. */
    public ResourcesTypesListResponseDataItem get(String key) {
        return t.request("GET", "/v1/resource_types/" + enc(key), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/resource_types/{key}} - Change a resource type's name, roles, parent type or inheritance. Scope: {@code resources:write}. */
    public ResourcesTypesListResponseDataItem update(String key, ResourcesTypesUpdateBody body) {
        return t.request("PATCH", "/v1/resource_types/" + enc(key), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/resource_types/{key}} - Delete a resource type together with its resources and their role assignments. Scope: {@code resources:write}. */
    public JsonNode delete(String key) {
        return t.request("DELETE", "/v1/resource_types/" + enc(key), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
