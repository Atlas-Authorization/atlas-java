package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ManagedGrantsListResponse;
import net.atlasauth.atlas.models.ManagedGrantsListResponseDataItem;
import net.atlasauth.atlas.models.ManagedGrantsNarrowBody;
import net.atlasauth.atlas.models.ManagedGrantsRevokeResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/grant_management} resource. */
public final class ManagedGrants {

    private final Transport t;

    public ManagedGrants(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/grant_management/grants} - List OpenID Grant Management grants: what each OAuth client holds for each user under a `grant_id`, newest first. Filter by `user_id`, `client_id` (the public client id) and `status`. Scope: {@code grants:read}. */
    public ManagedGrantsListResponse list() {
        return list(null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public ManagedGrantsListResponse list(String userId, String clientId, String status, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("user_id", userId).set("client_id", clientId).set("status", status).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/grant_management/grants", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/grant_management/grants/{id}} - Read one grant: its scopes, resource, authorization_details, status and revision. Scope: {@code grants:read}. */
    public ManagedGrantsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/grant_management/grants/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/grant_management/grants/{id}} - Narrow a grant: send the `scopes` and/or `authorization_details` it should keep. Anything it does not already hold is a 400 (only the user widens a grant, by authorizing again with `grant_management_action=merge`). Refresh tokens under the grant are clamped to the narrower scope; a revoked grant is a 404. Scope: {@code grants:write}. */
    public ManagedGrantsListResponseDataItem narrow(String id, ManagedGrantsNarrowBody body) {
        return t.request("PATCH", "/v1/grant_management/grants/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/grant_management/grants/{id}} - Revoke a grant and everything issued under it: refresh tokens stop refreshing and access tokens that carry the `grant_id` stop validating at once. `tokens_ended` counts the refresh grants that ended with it. Revoking an already-revoked grant answers 200 with `tokens_ended: 0`. Scope: {@code grants:write}. */
    public ManagedGrantsRevokeResponse revoke(String id) {
        return t.request("DELETE", "/v1/grant_management/grants/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
