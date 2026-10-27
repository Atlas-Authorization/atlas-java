package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.FraudLookup;
import net.atlasauth.atlas.models.FraudLookupBody;
import net.atlasauth.atlas.models.RiskEvent;
import net.atlasauth.atlas.models.RiskEventMetrics;
import net.atlasauth.atlas.models.UpdateRiskBasedMfaBody;

import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.models.RiskBasedMfaRemoveReputationSecretResponse;
import net.atlasauth.atlas.models.RiskBasedMfaSetReputationSecretBody;
import net.atlasauth.atlas.models.RiskBasedMfaSetReputationSecretResponse;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/risk_based_mfa} resource. */
public final class RiskBasedMfa {

    private final Transport t;

    public RiskBasedMfa(Transport t) {
        this.t = t;
    }

    /** Read the current adaptive-MFA config. */
    public net.atlasauth.atlas.models.RiskBasedMfa get() {
        return t.get("/v1/risk_based_mfa", new TypeReference<>() {
        });
    }

    /** Change any subset of the config; omitted fields are left unchanged. */
    public net.atlasauth.atlas.models.RiskBasedMfa update(UpdateRiskBasedMfaBody body) {
        return t.patch("/v1/risk_based_mfa", body, new TypeReference<>() {
        });
    }

    /** The risk-event timeline (every scored sign-in), newest first, with the server defaults. */
    public DataList<RiskEvent> listEvents() {
        return listEvents(null, null, null, null);
    }

    /**
     * The risk-event timeline, newest first.
     *
     * @param userId only this user's events.
     * @param level  {@code low}, {@code medium} or {@code high}.
     * @param limit  1..200 (default 50).
     * @param before ISO timestamp cursor on {@code created_at}; returns rows OLDER than this.
     */
    public DataList<RiskEvent> listEvents(String userId, String level, Integer limit, String before) {
        Map<String, Object> q = Params.of().set("user_id", userId).set("level", level).set("limit", limit)
                .set("before", before).map();
        return t.get("/v1/risk_events", q, new TypeReference<>() {
        });
    }

    /** Counts per level/action/mode over the last 7 days. */
    public RiskEventMetrics eventMetrics() {
        return eventMetrics(null);
    }

    /** Counts per level/action/mode over the last {@code days} (1..90, default 7). */
    public RiskEventMetrics eventMetrics(Integer days) {
        return t.get("/v1/risk_events/metrics", Params.of().set("days", days).map(), new TypeReference<>() {
        });
    }

    /**
     * Mark a scored sign-in {@code legit} or {@code fraud}; the weight of each signal that fired
     * moves accordingly.
     */
    public RiskEvent eventFeedback(String id, String verdict) {
        return t.post("/v1/risk_events/" + enc(id) + "/feedback", Map.of("verdict", verdict), new TypeReference<>() {
        });
    }

    /** Allow / challenge / block verdict for a sign-up, sign-in or checkout. */
    public FraudLookup fraudLookup(FraudLookupBody body) {
        return t.post("/v1/fraud/lookup", body, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/risk_based_mfa/reputation_secret} - Store (write-only, encrypted) the API key of your IP-reputation provider (ipqs, spur or minfraud; minFraud uses "&lt;account id&gt;:&lt;license key&gt;"). Then set `reputation.provider` on PATCH /v1/risk_based_mfa. Lookups are cached and fail open: a provider outage never blocks a sign-in. Scope: {@code risk_based_mfa:write}. */
    public RiskBasedMfaSetReputationSecretResponse setReputationSecret(RiskBasedMfaSetReputationSecretBody body) {
        return t.request("PUT", "/v1/risk_based_mfa/reputation_secret", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/risk_based_mfa/reputation_secret} - Remove a stored IP-reputation provider key (default: the configured provider). IP lookups for that provider stop; sign-in is unaffected. Scope: {@code risk_based_mfa:write}. */
    public RiskBasedMfaRemoveReputationSecretResponse removeReputationSecret() {
        return removeReputationSecret(null);
    }

    /** As above, with every optional parameter. */
    public RiskBasedMfaRemoveReputationSecretResponse removeReputationSecret(String provider) {
        Map<String, Object> q = Params.of().set("provider", provider).map();
        return t.request("DELETE", "/v1/risk_based_mfa/reputation_secret", q, null, null, false, new TypeReference<>() {
        });
    }
}
