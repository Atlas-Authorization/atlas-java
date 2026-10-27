package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.GroupsAddMembersBody;
import net.atlasauth.atlas.models.GroupsCreateBody;
import net.atlasauth.atlas.models.GroupsDeleteResponse;
import net.atlasauth.atlas.models.GroupsListMembersResponse;
import net.atlasauth.atlas.models.GroupsListResponse;
import net.atlasauth.atlas.models.GroupsListResponseDataItem;
import net.atlasauth.atlas.models.GroupsReapplyResponse;
import net.atlasauth.atlas.models.GroupsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/groups} resource. */
public final class Groups {

    private final Transport t;

    public Groups(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/groups} - Every instance group with its member count, by slug. Scope: {@code users:read}. */
    public GroupsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public GroupsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/groups", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/groups} - Create an instance group. Emits group.created. -&gt; 201. Scope: {@code users:write}. */
    public GroupsListResponseDataItem create(GroupsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public GroupsListResponseDataItem create(GroupsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/groups", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/groups/{id}} - One group, by id or slug. Scope: {@code users:read}. */
    public GroupsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/groups/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/groups/{id}} - Change a group. Emits group.updated. Scope: {@code users:write}. */
    public GroupsListResponseDataItem update(String id, GroupsUpdateBody body) {
        return t.request("PATCH", "/v1/groups/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/groups/{id}} - Delete a group: its memberships go, its children move to the top level. Emits group.deleted. Scope: {@code users:write}. */
    public GroupsDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/groups/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/groups/{id}/members} - The members of one group (direct members only), with how each joined (manual, dynamic, sso, rule). Cursor paged by user id. Scope: {@code users:read}. */
    public GroupsListMembersResponse listMembers(String id) {
        return listMembers(id, null, null);
    }

    /** As above, with every optional parameter. */
    public GroupsListMembersResponse listMembers(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/groups/" + enc(id) + "/members", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/groups/{id}/members} - Add up to 500 users by hand. A member who joined by filter, SSO or rule becomes manual, so a re-apply leaves them. Emits group.member_added per new member. Scope: {@code users:write}. */
    public JsonNode addMembers(String id, GroupsAddMembersBody body) {
        return addMembers(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode addMembers(String id, GroupsAddMembersBody body, String idempotencyKey) {
        return t.request("POST", "/v1/groups/" + enc(id) + "/members", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/groups/{id}/members/remove} - Remove up to 500 users from a group. Emits group.member_removed. Scope: {@code users:write}. */
    public JsonNode removeMembers(String id, GroupsAddMembersBody body) {
        return removeMembers(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode removeMembers(String id, GroupsAddMembersBody body, String idempotencyKey) {
        return t.request("POST", "/v1/groups/" + enc(id) + "/members/remove", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/groups/{id}/reapply} - Re-evaluate a dynamic group now: users matching dynamic_filter are added, those who no longer match are removed; manual members are never touched (400 when the group has no dynamic_filter, or the filter matches over 50,000 users). Scope: {@code users:write}. */
    public GroupsReapplyResponse reapply(String id) {
        return reapply(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public GroupsReapplyResponse reapply(String id, String idempotencyKey) {
        return t.request("POST", "/v1/groups/" + enc(id) + "/reapply", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/groups} - A user's direct groups and how they joined each. Scope: {@code users:read}. */
    public GroupsListResponse forUser(String id) {
        return forUser(id, null, null);
    }

    /** As above, with every optional parameter. */
    public GroupsListResponse forUser(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/users/" + enc(id) + "/groups", q, null, null, false, new TypeReference<>() {
        });
    }

    /** The {@code rules} sub-resource. */
    public GroupsRules rules() {
        return new GroupsRules(t);
    }
}
