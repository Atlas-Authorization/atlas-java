package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AccessPoliciesCreateBody;
import net.atlasauth.atlas.models.AccessPoliciesListResponse;
import net.atlasauth.atlas.models.AccessPoliciesListResponseDataItem;
import net.atlasauth.atlas.models.AccessPoliciesSimulateBody;
import net.atlasauth.atlas.models.AccessPoliciesSimulateResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/access_policies} resource. */
public final class AccessPolicies {

    private final Transport t;

    public AccessPolicies(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/access_policies} - List conditional-access policies, in evaluation order. Scope: {@code access_policies:read}. */
    public AccessPoliciesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public AccessPoliciesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/access_policies", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/access_policies} - Create a conditional-access policy. `conditions` (applications, userIds, groups, orgs, roles, networkZones &#123;include, exclude&#125;, countries, riskLevels, deviceTrust, authMethods, timeWindow) are ANDed; an empty condition matches anything. `grants` are allow, block, require_mfa, require_acr, require_policy_acceptance or require_action. `stages` limits it to sign_in, authorize and/or token (default all). A matching block wins; step-up grants are unioned. Scope: {@code access_policies:write}. */
    public AccessPoliciesListResponseDataItem create(AccessPoliciesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AccessPoliciesListResponseDataItem create(AccessPoliciesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/access_policies", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/access_policies/simulate} - What-if: describe a request (stage, ip, application, orgs, roles, country, risk level, device trust, auth methods, whether MFA was verified) and see the decision, the policies that matched and the unmet step-up requirements. Pass `policy_id` to test one policy even if it is disabled. Scope: {@code access_policies:read}. */
    public AccessPoliciesSimulateResponse simulate(AccessPoliciesSimulateBody body) {
        return simulate(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AccessPoliciesSimulateResponse simulate(AccessPoliciesSimulateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/access_policies/simulate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/access_policies/{id}} - Read one access policy. Scope: {@code access_policies:read}. */
    public AccessPoliciesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/access_policies/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/access_policies/{id}} - Update an access policy. Sent fields replace the stored value. Scope: {@code access_policies:write}. */
    public AccessPoliciesListResponseDataItem update(String id, AccessPoliciesCreateBody body) {
        return t.request("PATCH", "/v1/access_policies/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/access_policies/{id}} - Delete an access policy. Scope: {@code access_policies:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/access_policies/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
