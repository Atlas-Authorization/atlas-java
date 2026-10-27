package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.FlagsAssignOrganizationEntitlementsBody;
import net.atlasauth.atlas.models.FlagsAssignOrganizationEntitlementsResponse;
import net.atlasauth.atlas.models.FlagsCreateBody;
import net.atlasauth.atlas.models.FlagsCreateResponse;
import net.atlasauth.atlas.models.FlagsDeleteResponse;
import net.atlasauth.atlas.models.FlagsEvaluateBody;
import net.atlasauth.atlas.models.FlagsEvaluateResponse;
import net.atlasauth.atlas.models.FlagsListResponse;
import net.atlasauth.atlas.models.FlagsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/feature_flags} resource. */
public final class Flags {

    private final Transport t;

    public Flags(Transport t) {
        this.t = t;
    }

    /** {@code PUT /v1/organizations/{id}/entitlements} - Assign an organization's entitlements (a plan, a feature list and/or limits, optionally expiring) without a billing subscription. Replaces whatever was assigned or overridden before; an empty body clears it and the organization falls back to its billing plan. Reaches the `pla`, `fea` and `ent` session claims on the next token. Scope: {@code billing:write}. */
    public FlagsAssignOrganizationEntitlementsResponse assignOrganizationEntitlements(String id, FlagsAssignOrganizationEntitlementsBody body) {
        return t.request("PUT", "/v1/organizations/" + enc(id) + "/entitlements", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/feature_flags} - Create a feature flag: a typed value (`boolean`, `string`, `int` or `json`) with a `default_value`, an `off_value` (served while `enabled` is false; defaults to false for a boolean, else the default) and ordered targeting `rules` by org, user, plan and percentage. 409 when the key exists. Scope: {@code flags:write}. */
    public FlagsCreateResponse create(FlagsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public FlagsCreateResponse create(FlagsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/feature_flags", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/feature_flags} - List feature flags ordered by key. `starting_after` is the key of the last flag of the previous page. Scope: {@code flags:read}. */
    public FlagsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public FlagsListResponse list(String startingAfter, Long limit) {
        Map<String, Object> q = Params.of().set("starting_after", startingAfter).set("limit", limit).map();
        return t.request("GET", "/v1/feature_flags", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/feature_flags/{key}} - Read one feature flag. Scope: {@code flags:read}. */
    public FlagsCreateResponse retrieve(String key) {
        return t.request("GET", "/v1/feature_flags/" + enc(key), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/feature_flags/{key}} - Change a flag's description, kill switch, default or off value, or replace its rules. The type and key are fixed. Scope: {@code flags:write}. */
    public FlagsCreateResponse update(String key, FlagsUpdateBody body) {
        return t.request("PATCH", "/v1/feature_flags/" + enc(key), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/feature_flags/{key}} - Delete a feature flag. Scope: {@code flags:write}. */
    public FlagsDeleteResponse delete(String key) {
        return t.request("DELETE", "/v1/feature_flags/" + enc(key), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/feature_flags/evaluate} - Evaluate every flag (or the listed `keys`) for a subject (`user_id`, `org_id`, `plan`). When `plan` is omitted it is the organization's (else the user's) resolved plan. Each result carries the `value` and a `reason` (`off`, `default` or `rule:&lt;index&gt;`). Scope: {@code flags:read}. */
    public FlagsEvaluateResponse evaluate(FlagsEvaluateBody body) {
        return evaluate(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public FlagsEvaluateResponse evaluate(FlagsEvaluateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/feature_flags/evaluate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/users/{id}/entitlements} - Assign a user's entitlements the same way as for an organization. Scope: {@code billing:write}. */
    public FlagsAssignOrganizationEntitlementsResponse assignUserEntitlements(String id, FlagsAssignOrganizationEntitlementsBody body) {
        return t.request("PUT", "/v1/users/" + enc(id) + "/entitlements", null, body, null, false, new TypeReference<>() {
        });
    }
}
