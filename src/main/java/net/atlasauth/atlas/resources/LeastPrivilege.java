package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.LeastPrivilegeApplyBody;
import net.atlasauth.atlas.models.LeastPrivilegeApplyResponse;
import net.atlasauth.atlas.models.LeastPrivilegeReportResponse;
import net.atlasauth.atlas.models.LeastPrivilegeReportResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/least_privilege} resource. */
public final class LeastPrivilege {

    private final Transport t;

    public LeastPrivilege(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/least_privilege/report} - The least-privilege report: for every active secret key, OAuth client, MCP gateway and agent, what it is allowed to do against what it used over the window, the permissions never used, and the narrower grant that would still cover its use. Credentials with something unused come first. `format=csv` exports one row per permission. Scope: {@code least_privilege:read}. */
    public LeastPrivilegeReportResponse report() {
        return report(null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public LeastPrivilegeReportResponse report(Long days, String subjectType, String subjectId, Boolean onlyUnused, Long limit, String format, String startingAfter) {
        Map<String, Object> q = Params.of().set("days", days).set("subject_type", subjectType).set("subject_id", subjectId).set("only_unused", onlyUnused).set("limit", limit).set("format", format).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/least_privilege/report", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/least_privilege/report/{type}/{id}} - The least-privilege report of one credential (`type` is api_key, oauth_client, mcp_gateway or agent). 404 for a credential the report does not cover, such as a revoked key. Scope: {@code least_privilege:read}. */
    public LeastPrivilegeReportResponseDataItem get(String type, String id) {
        return get(type, id, null);
    }

    /** As above, with every optional parameter. */
    public LeastPrivilegeReportResponseDataItem get(String type, String id, Long days) {
        Map<String, Object> q = Params.of().set("days", days).map();
        return t.request("GET", "/v1/least_privilege/report/" + enc(type) + "/" + enc(id), q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/least_privilege/apply} - Apply a credential's suggestion: narrow a secret key's scopes, an OAuth client's allowed scopes, an MCP gateway's tool policy, or revoke an agent's unused tool grants. Preview it first with `?dry_run=true`, then send the same body for real. `digest` (from the report) must match the current suggestion, else 409, so what you previewed is what is applied; the write is conditional on the credential being unchanged. A suggestion only ever removes permissions. Over a window the data does not fully cover (`confidence: low`) it needs `acknowledge_short_window: true`. Audited as `least_privilege.applied`. Scope: {@code least_privilege:write}. */
    public LeastPrivilegeApplyResponse apply(LeastPrivilegeApplyBody body) {
        return apply(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public LeastPrivilegeApplyResponse apply(LeastPrivilegeApplyBody body, String idempotencyKey) {
        return t.request("POST", "/v1/least_privilege/apply", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
