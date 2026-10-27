package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ResourcesAssignmentsAssignBody;
import net.atlasauth.atlas.models.ResourcesAssignmentsListResponse;
import net.atlasauth.atlas.models.ResourcesAssignmentsListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/resource_role_assignments} resource. */
public final class ResourcesAssignments {

    private final Transport t;

    public ResourcesAssignments(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/resource_role_assignments} - List role assignments, filtered by `resource_id`, `subject_type` or `subject_id`. Scope: {@code resources:read}. */
    public ResourcesAssignmentsListResponse list() {
        return list(null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public ResourcesAssignmentsListResponse list(String resourceId, String subjectType, String subjectId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("resource_id", resourceId).set("subject_type", subjectType).set("subject_id", subjectId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/resource_role_assignments", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/resource_role_assignments} - Assign a `role` on a `resource` to a `subject` (`&#123;type: user|group|org, id&#125;`). `inherit` (default true) also applies it to the resource's descendants. Re-assigning the same role updates `inherit`. Scope: {@code resources:write}. */
    public ResourcesAssignmentsListResponseDataItem assign(ResourcesAssignmentsAssignBody body) {
        return assign(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ResourcesAssignmentsListResponseDataItem assign(ResourcesAssignmentsAssignBody body, String idempotencyKey) {
        return t.request("POST", "/v1/resource_role_assignments", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/resource_role_assignments/{id}} - Revoke a role assignment. Scope: {@code resources:write}. */
    public JsonNode revoke(String id) {
        return t.request("DELETE", "/v1/resource_role_assignments/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
