package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AnalyticsAlertRulesCreateBody;
import net.atlasauth.atlas.models.AnalyticsAlertRulesDeleteResponse;
import net.atlasauth.atlas.models.AnalyticsAlertRulesListResponse;
import net.atlasauth.atlas.models.AnalyticsAlertRulesListResponseDataItem;
import net.atlasauth.atlas.models.AnalyticsAlertRulesTestBody;
import net.atlasauth.atlas.models.AnalyticsAlertRulesTestResponse;
import net.atlasauth.atlas.models.AnalyticsAlertRulesUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/analytics} resource. */
public final class AnalyticsAlertRules {

    private final Transport t;

    public AnalyticsAlertRules(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/analytics/alert_rules} - List analytics alert rules with their state and last measured value. The metric catalog is included. Scope: {@code analytics:read}. */
    public AnalyticsAlertRulesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsAlertRulesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/alert_rules", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/analytics/alert_rules} - Create an alert rule: tell me when a sign-in health metric, or a key or organization's API usage against its baseline, crosses a threshold over a trailing window. It must have email_contacts, a webhook_url or ssf. A webhook signing secret is returned once. Scope: {@code analytics:write}. */
    public AnalyticsAlertRulesListResponseDataItem create(AnalyticsAlertRulesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AnalyticsAlertRulesListResponseDataItem create(AnalyticsAlertRulesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/analytics/alert_rules", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/alert_rules/{id}} - One analytics alert rule. Scope: {@code analytics:read}. */
    public AnalyticsAlertRulesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/analytics/alert_rules/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/analytics/alert_rules/{id}} - Edit an alert rule. Editing re-arms it. Scope: {@code analytics:write}. */
    public AnalyticsAlertRulesListResponseDataItem update(String id, AnalyticsAlertRulesUpdateBody body) {
        return t.request("PATCH", "/v1/analytics/alert_rules/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/analytics/alert_rules/{id}} - Delete an alert rule. Scope: {@code analytics:write}. */
    public AnalyticsAlertRulesDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/analytics/alert_rules/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/analytics/alert_rules/{id}/test} - Measure the rule now. With `notify: true` it also sends the rule's email and webhook, whether or not the rule is breaching, to prove the delivery path. Scope: {@code analytics:write}. */
    public AnalyticsAlertRulesTestResponse test(String id, AnalyticsAlertRulesTestBody body) {
        return test(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AnalyticsAlertRulesTestResponse test(String id, AnalyticsAlertRulesTestBody body, String idempotencyKey) {
        return t.request("POST", "/v1/analytics/alert_rules/" + enc(id) + "/test", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
