package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IgaLifecycleCreateBirthrightRuleBody;
import net.atlasauth.atlas.models.IgaLifecycleDeleteBirthrightRuleResponse;
import net.atlasauth.atlas.models.IgaLifecycleListBirthrightRulesResponse;
import net.atlasauth.atlas.models.IgaLifecycleListBirthrightRulesResponseDataItem;
import net.atlasauth.atlas.models.IgaLifecycleOffboardBody;
import net.atlasauth.atlas.models.IgaLifecycleOffboardResponse;
import net.atlasauth.atlas.models.IgaLifecycleReconcileResponse;
import net.atlasauth.atlas.models.IgaLifecycleSettingsResponse;
import net.atlasauth.atlas.models.IgaLifecycleUpdateBirthrightRuleBody;
import net.atlasauth.atlas.models.IgaLifecycleUpdateSettingsBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/iga} resource. */
public final class IgaLifecycle {

    private final Transport t;

    public IgaLifecycle(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/iga/birthright_rules} - List birthright rules. Scope: {@code iga:read}. */
    public IgaLifecycleListBirthrightRulesResponse listBirthrightRules() {
        return listBirthrightRules(null, null);
    }

    /** As above, with every optional parameter. */
    public IgaLifecycleListBirthrightRulesResponse listBirthrightRules(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/birthright_rules", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/birthright_rules} - Create a birthright rule: everyone matching `match` (a group, organization and role, app role or verified email domain; all conditions must hold) holds the listed entitlements. When someone stops matching, the grants the rule gave are revoked unless `revoke_on_unmatch` is false. Scope: {@code iga:write}. */
    public IgaLifecycleListBirthrightRulesResponseDataItem createBirthrightRule(IgaLifecycleCreateBirthrightRuleBody body) {
        return createBirthrightRule(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaLifecycleListBirthrightRulesResponseDataItem createBirthrightRule(IgaLifecycleCreateBirthrightRuleBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/birthright_rules", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/iga/birthright_rules/{id}} - Change a birthright rule. Scope: {@code iga:write}. */
    public IgaLifecycleListBirthrightRulesResponseDataItem updateBirthrightRule(String id, IgaLifecycleUpdateBirthrightRuleBody body) {
        return t.request("PATCH", "/v1/iga/birthright_rules/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/iga/birthright_rules/{id}} - Delete a birthright rule (grants it already gave stay until revoked). Scope: {@code iga:write}. */
    public IgaLifecycleDeleteBirthrightRuleResponse deleteBirthrightRule(String id) {
        return t.request("DELETE", "/v1/iga/birthright_rules/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/users/{id}/reconcile} - Re-evaluate one user's birthright access now (joiner or mover). Scope: {@code iga:write}. */
    public IgaLifecycleReconcileResponse reconcile(String id) {
        return reconcile(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaLifecycleReconcileResponse reconcile(String id, String idempotencyKey) {
        return t.request("POST", "/v1/iga/users/" + enc(id) + "/reconcile", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/users/{id}/offboard} - Offboard a leaver: revoke every grant, cancel pending requests and delegations, and with `close_account` schedule the account closure through the data-rights path (so downstream erasure webhooks and any DSAR orchestrator run as for any deletion). `grace_seconds` sets the cancellation window. Scope: {@code iga:write}. */
    public IgaLifecycleOffboardResponse offboard(String id, IgaLifecycleOffboardBody body) {
        return offboard(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaLifecycleOffboardResponse offboard(String id, IgaLifecycleOffboardBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/users/" + enc(id) + "/offboard", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/settings} - Joiner/mover/leaver settings. Scope: {@code iga:read}. */
    public IgaLifecycleSettingsResponse settings() {
        return t.request("GET", "/v1/iga/settings", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/iga/settings} - Turn the joiner/mover/leaver automation on or off and choose what a deactivated account triggers: `revoke` (end the grants) or `revoke_and_close` (also schedule the closure after `leaver_close_grace_seconds`). Scope: {@code iga:write}. */
    public IgaLifecycleSettingsResponse updateSettings(IgaLifecycleUpdateSettingsBody body) {
        return t.request("PUT", "/v1/iga/settings", null, body, null, false, new TypeReference<>() {
        });
    }
}
