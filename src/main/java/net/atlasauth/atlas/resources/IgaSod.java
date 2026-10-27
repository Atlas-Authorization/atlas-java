package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IgaSodCreateRuleBody;
import net.atlasauth.atlas.models.IgaSodDeleteRuleResponse;
import net.atlasauth.atlas.models.IgaSodListRulesResponse;
import net.atlasauth.atlas.models.IgaSodListRulesResponseDataItem;
import net.atlasauth.atlas.models.IgaSodScanResponse;
import net.atlasauth.atlas.models.IgaSodUpdateRuleBody;
import net.atlasauth.atlas.models.IgaSodViolationsResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/iga} resource. */
public final class IgaSod {

    private final Transport t;

    public IgaSod(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/iga/sod_rules} - List separation-of-duties rules: pairs of entitlements one person should not hold together. Scope: {@code iga:read}. */
    public IgaSodListRulesResponse listRules() {
        return listRules(null, null);
    }

    /** As above, with every optional parameter. */
    public IgaSodListRulesResponse listRules(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/sod_rules", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/sod_rules} - Create a rule. `mode` is `block` (refuse) or `require_approval` (an extra approval step on a request; `approver` names who, default the owner). Scope: {@code iga:write}. */
    public IgaSodListRulesResponseDataItem createRule(IgaSodCreateRuleBody body) {
        return createRule(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaSodListRulesResponseDataItem createRule(IgaSodCreateRuleBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/sod_rules", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/iga/sod_rules/{id}} - Change a separation-of-duties rule. Scope: {@code iga:write}. */
    public IgaSodListRulesResponseDataItem updateRule(String id, IgaSodUpdateRuleBody body) {
        return t.request("PATCH", "/v1/iga/sod_rules/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/iga/sod_rules/{id}} - Delete a rule and its recorded violations. Scope: {@code iga:write}. */
    public IgaSodDeleteRuleResponse deleteRule(String id) {
        return t.request("DELETE", "/v1/iga/sod_rules/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/sod_violations} - The violations report: users who hold both sides of a rule (found by a scan, or accepted at request or assignment time). Scope: {@code iga:read}. */
    public IgaSodViolationsResponse violations() {
        return violations(null, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaSodViolationsResponse violations(String status, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("status", status).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/sod_violations", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/sod_violations/scan} - Check every rule against what people hold right now: record new conflicts and resolve those that cleared. Scope: {@code iga:write}. */
    public IgaSodScanResponse scan() {
        return scan(null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaSodScanResponse scan(String idempotencyKey) {
        return t.request("POST", "/v1/iga/sod_violations/scan", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
