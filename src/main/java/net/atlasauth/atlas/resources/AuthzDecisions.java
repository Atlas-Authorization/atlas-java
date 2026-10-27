package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AuthzDecisionsListResponse;
import net.atlasauth.atlas.models.AuthzDecisionsListResponseDataItem;
import net.atlasauth.atlas.models.AuthzDecisionsStatsResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/authz_decisions} resource. */
public final class AuthzDecisions {

    private final Transport t;

    public AuthzDecisions(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/authz_decisions} - Query the authorization decision log, newest first: ABAC, AuthZEN, FGA-check and access-policy decisions with their redacted input, result, the policy revision in force and latency. Only decisions that passed sampling are stored. Scope: {@code authz_decisions:read}. */
    public AuthzDecisionsListResponse list() {
        return list(null, null, null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AuthzDecisionsListResponse list(String source, String decision, String principal, String action, String resource, String policyRevision, String since, String until, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("source", source).set("decision", decision).set("principal", principal).set("action", action).set("resource", resource).set("policy_revision", policyRevision).set("since", since).set("until", until).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/authz_decisions", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/authz_decisions/stats} - Decision counts per engine and outcome over a window (the last 24 hours by default), with a sampling-corrected estimate of the true volume and latency percentiles of the recorded decisions. Scope: {@code authz_decisions:read}. */
    public AuthzDecisionsStatsResponse stats() {
        return stats(null, null);
    }

    /** As above, with every optional parameter. */
    public AuthzDecisionsStatsResponse stats(String since, String until) {
        Map<String, Object> q = Params.of().set("since", since).set("until", until).map();
        return t.request("GET", "/v1/authz_decisions/stats", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/authz_decisions/{id}} - Read one logged decision. Scope: {@code authz_decisions:read}. */
    public AuthzDecisionsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/authz_decisions/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** The {@code settings} sub-resource. */
    public AuthzDecisionsSettings settings() {
        return new AuthzDecisionsSettings(t);
    }
}
