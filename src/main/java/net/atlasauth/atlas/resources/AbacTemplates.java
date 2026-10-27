package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AbacTemplatesCreateBody;
import net.atlasauth.atlas.models.AbacTemplatesListResponse;
import net.atlasauth.atlas.models.AbacTemplatesListResponseDataItem;
import net.atlasauth.atlas.models.AbacTemplatesUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/abac_templates} resource. */
public final class AbacTemplates {

    private final Transport t;

    public AbacTemplates(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/abac_templates} - List the ABAC policy templates, with their slots and how many links each has. Scope: {@code abac:read}. */
    public AbacTemplatesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public AbacTemplatesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/abac_templates", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac_templates} - Create a policy template: policy source whose scope uses the slots `?principal` and/or `?resource` (`principal == ?principal`, `resource in ?resource`, `resource is Doc in ?resource`). Slots are filled by a link. With an entity schema set, the template is type-checked on save (a slot stands for any entity type). Scope: {@code abac:write}. */
    public AbacTemplatesListResponseDataItem create(AbacTemplatesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacTemplatesListResponseDataItem create(AbacTemplatesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac_templates", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/abac_templates/{id}} - Read one policy template. Scope: {@code abac:read}. */
    public AbacTemplatesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/abac_templates/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/abac_templates/{id}} - Change a template. A new `source` bumps `version` and takes effect on every link at once (`links_updated` counts them). It is refused, with one error per problem and the link named, if any link could no longer be filled in or would fail the entity schema. Scope: {@code abac:write}. */
    public AbacTemplatesListResponseDataItem update(String id, AbacTemplatesUpdateBody body) {
        return t.request("PATCH", "/v1/abac_templates/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/abac_templates/{id}} - Delete a template. A template with links is refused with 409 unless `force=true`, which deletes the links too. Scope: {@code abac:write}. */
    public JsonNode delete(String id) {
        return delete(id, null);
    }

    /** As above, with every optional parameter. */
    public JsonNode delete(String id, Boolean force) {
        Map<String, Object> q = Params.of().set("force", force).map();
        return t.request("DELETE", "/v1/abac_templates/" + enc(id), q, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
