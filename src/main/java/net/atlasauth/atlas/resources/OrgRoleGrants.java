package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OrgRoleGrantsCreateBody;
import net.atlasauth.atlas.models.OrgRoleGrantsDelegateBody;
import net.atlasauth.atlas.models.OrgRoleGrantsEffectiveResponse;
import net.atlasauth.atlas.models.OrgRoleGrantsListDelegationsResponse;
import net.atlasauth.atlas.models.OrgRoleGrantsListDelegationsResponseDataItem;
import net.atlasauth.atlas.models.OrgRoleGrantsListResponse;
import net.atlasauth.atlas.models.OrgRoleGrantsListResponseDataItem;
import net.atlasauth.atlas.models.OrgRoleGrantsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/org_role_grants} resource. */
public final class OrgRoleGrants {

    private final Transport t;

    public OrgRoleGrants(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/org_role_grants} - List cross-organization role grants, filtered by grantor_org_id, grantee_org_id or status=active. Scope: {@code organizations:read}. */
    public OrgRoleGrantsListResponse list() {
        return list(null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public OrgRoleGrantsListResponse list(String grantorOrgId, String granteeOrgId, String status, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("grantor_org_id", grantorOrgId).set("grantee_org_id", granteeOrgId).set("status", status).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/org_role_grants", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/org_role_grants} - Let a grantee organization hand out a bounded set of the grantor organization's roles (role_keys) to its own members. One active grant per pair. Scope: {@code organizations:write}. */
    public OrgRoleGrantsListResponseDataItem create(OrgRoleGrantsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public OrgRoleGrantsListResponseDataItem create(OrgRoleGrantsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/org_role_grants", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/org_role_grants/{id}} - Fetch one cross-organization role grant. Scope: {@code organizations:read}. */
    public OrgRoleGrantsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/org_role_grants/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/org_role_grants/{id}} - Replace the granted role_keys. Delegations of roles no longer granted are removed. Scope: {@code organizations:write}. */
    public OrgRoleGrantsListResponseDataItem update(String id, OrgRoleGrantsUpdateBody body) {
        return t.request("PATCH", "/v1/org_role_grants/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/org_role_grants/{id}} - Revoke a grant. Every delegation under it stops taking effect immediately. Scope: {@code organizations:write}. */
    public OrgRoleGrantsListResponseDataItem revoke(String id) {
        return t.request("DELETE", "/v1/org_role_grants/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/org_role_grants/{id}/delegations} - List the members holding roles under a grant. Scope: {@code organizations:read}. */
    public OrgRoleGrantsListDelegationsResponse listDelegations(String id) {
        return listDelegations(id, null, null);
    }

    /** As above, with every optional parameter. */
    public OrgRoleGrantsListDelegationsResponse listDelegations(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/org_role_grants/" + enc(id) + "/delegations", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/org_role_grants/{id}/delegations} - Delegate one granted role_key to a member (user_id) of the grantee organization. Scope: {@code organizations:write}. */
    public OrgRoleGrantsListDelegationsResponseDataItem delegate(String id, OrgRoleGrantsDelegateBody body) {
        return delegate(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public OrgRoleGrantsListDelegationsResponseDataItem delegate(String id, OrgRoleGrantsDelegateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/org_role_grants/" + enc(id) + "/delegations", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/org_role_grants/{id}/delegations/{delegationId}} - Remove a delegation of a granted role. Scope: {@code organizations:write}. */
    public JsonNode undelegate(String id, String delegationId) {
        return t.request("DELETE", "/v1/org_role_grants/" + enc(id) + "/delegations/" + enc(delegationId), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/org_role_grants/effective} - The roles and permissions a user (user_id) holds in a grantor organization (organization_id, optional) through active delegations. Scope: {@code organizations:read}. */
    public OrgRoleGrantsEffectiveResponse effective(String userId) {
        return effective(userId, null, null, null);
    }

    /** As above, with every optional parameter. */
    public OrgRoleGrantsEffectiveResponse effective(String userId, String organizationId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("user_id", userId).set("organization_id", organizationId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/org_role_grants/effective", q, null, null, false, new TypeReference<>() {
        });
    }
}
