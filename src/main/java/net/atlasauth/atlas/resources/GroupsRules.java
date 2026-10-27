package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.GroupsRulesApplyAllResponse;
import net.atlasauth.atlas.models.GroupsRulesApplyAllResponseDataItem;
import net.atlasauth.atlas.models.GroupsRulesCreateBody;
import net.atlasauth.atlas.models.GroupsRulesDeleteResponse;
import net.atlasauth.atlas.models.GroupsRulesListResponse;
import net.atlasauth.atlas.models.GroupsRulesListResponseDataItem;
import net.atlasauth.atlas.models.GroupsRulesUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/group_rules} resource. */
public final class GroupsRules {

    private final Transport t;

    public GroupsRules(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/group_rules} - Group rules, lowest priority number first. Scope: {@code users:read}. */
    public GroupsRulesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public GroupsRulesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/group_rules", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/group_rules} - Create a rule: when a user matches condition, run the actions (add to a group, grant an app role, set a trait). -&gt; 201. Scope: {@code users:write}. */
    public GroupsRulesListResponseDataItem create(GroupsRulesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public GroupsRulesListResponseDataItem create(GroupsRulesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/group_rules", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/group_rules/apply} - Run every enabled rule in priority order. One result per rule. Scope: {@code users:write}. */
    public GroupsRulesApplyAllResponse applyAll() {
        return applyAll(null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public GroupsRulesApplyAllResponse applyAll(String idempotencyKey) {
        return t.request("POST", "/v1/group_rules/apply", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/group_rules/{id}} - One group rule, with its condition and actions. Scope: {@code users:read}. */
    public GroupsRulesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/group_rules/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/group_rules/{id}} - Change a group rule, its condition or actions. Scope: {@code users:write}. */
    public GroupsRulesListResponseDataItem update(String id, GroupsRulesUpdateBody body) {
        return t.request("PATCH", "/v1/group_rules/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/group_rules/{id}} - Delete a rule. Memberships it created stay until removed. Scope: {@code users:write}. */
    public GroupsRulesDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/group_rules/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/group_rules/{id}/apply} - Run one rule against every matching user now. Returns the match count and what changed. Scope: {@code users:write}. */
    public GroupsRulesApplyAllResponseDataItem apply(String id) {
        return apply(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public GroupsRulesApplyAllResponseDataItem apply(String id, String idempotencyKey) {
        return t.request("POST", "/v1/group_rules/" + enc(id) + "/apply", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
