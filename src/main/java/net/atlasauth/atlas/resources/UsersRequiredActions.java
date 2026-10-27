package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UsersRequiredActionsBulkBody;
import net.atlasauth.atlas.models.UsersRequiredActionsClearResponse;
import net.atlasauth.atlas.models.UsersRequiredActionsListResponse;
import net.atlasauth.atlas.models.UsersRequiredActionsRequestBody;
import net.atlasauth.atlas.models.UsersRequiredActionsRequestResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class UsersRequiredActions {

    private final Transport t;

    public UsersRequiredActions(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/users/{id}/required_actions} - Make a user owe an action at their next sign-in, whatever the method: update_password, verify_email, accept_policy, configure_mfa, update_profile, or custom (sent to an https url; the app closes it with DELETE). The sign-in attempt holds at needs_required_action until it is done (configure_mfa alone routes through MFA enrollment; accept_policy is closed once the policy gate passes; verify_email and configure_mfa clear themselves when already true). Re-requesting an open action is a 200 with the same row. Emits user.required_action_requested. -&gt; 201. Scope: {@code users:write}. */
    public UsersRequiredActionsRequestResponse request(String id, UsersRequiredActionsRequestBody body) {
        return request(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersRequiredActionsRequestResponse request(String id, UsersRequiredActionsRequestBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/required_actions", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/required_actions} - The actions a user currently owes, oldest first. Scope: {@code users:read}. */
    public UsersRequiredActionsListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public UsersRequiredActionsListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/users/" + enc(id) + "/required_actions", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/required_actions/{rid}} - Withdraw an owed action. This is also how an application closes a custom action once the user has finished its step. Emits user.required_action_cleared. 404 when it is not open. Scope: {@code users:write}. */
    public UsersRequiredActionsClearResponse clear(String id, String rid) {
        return t.request("DELETE", "/v1/users/" + enc(id) + "/required_actions/" + enc(rid), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/required_actions/bulk} - Owe the same action for many users at once: every member of one organization (organization_id) or an explicit user_ids list, at most 5000. Users outside the instance are skipped, and an already-open action counts as already_open. Emits user.required_action_requested per newly opened action. Scope: {@code users:write}. */
    public JsonNode bulk(UsersRequiredActionsBulkBody body) {
        return bulk(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode bulk(UsersRequiredActionsBulkBody body, String idempotencyKey) {
        return t.request("POST", "/v1/required_actions/bulk", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }
}
