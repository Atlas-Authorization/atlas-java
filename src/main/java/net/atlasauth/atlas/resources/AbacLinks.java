package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AbacLinksCreateBody;
import net.atlasauth.atlas.models.AbacLinksListResponse;
import net.atlasauth.atlas.models.AbacLinksListResponseDataItem;
import net.atlasauth.atlas.models.AbacLinksUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/abac_policy_links} resource. */
public final class AbacLinks {

    private final Transport t;

    public AbacLinks(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/abac_policy_links} - List template links: a template with its slots filled by entities. Each link's `source` is what the policy engine evaluates. Scope: {@code abac:read}. */
    public AbacLinksListResponse list() {
        return list(null, null, null);
    }

    /** As above, with every optional parameter. */
    public AbacLinksListResponse list(String templateId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("template_id", templateId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/abac_policy_links", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac_policy_links} - Link a template to entities. Every slot the template uses needs its entity (`principal` for `?principal`, `resource` for `?resource`); one it does not use is refused. The link is evaluated together with the other enabled policies, always from the template's current source, and is type-checked against the entity schema when one is set. Scope: {@code abac:write}. */
    public AbacLinksListResponseDataItem create(AbacLinksCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacLinksListResponseDataItem create(AbacLinksCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac_policy_links", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/abac_policy_links/{id}} - Read one template link. Scope: {@code abac:read}. */
    public AbacLinksListResponseDataItem get(String id) {
        return t.request("GET", "/v1/abac_policy_links/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/abac_policy_links/{id}} - Rename, enable or disable a link, or point its slots at other entities. Scope: {@code abac:write}. */
    public AbacLinksListResponseDataItem update(String id, AbacLinksUpdateBody body) {
        return t.request("PATCH", "/v1/abac_policy_links/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/abac_policy_links/{id}} - Delete a link; the template stays. Scope: {@code abac:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/abac_policy_links/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
