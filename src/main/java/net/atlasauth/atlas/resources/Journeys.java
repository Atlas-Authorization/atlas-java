package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.JourneysActivateBody;
import net.atlasauth.atlas.models.JourneysCreateBody;
import net.atlasauth.atlas.models.JourneysListResponse;
import net.atlasauth.atlas.models.JourneysListResponseDataItem;
import net.atlasauth.atlas.models.JourneysSimulateBody;
import net.atlasauth.atlas.models.JourneysSimulateResponse;
import net.atlasauth.atlas.models.JourneysUpdateBody;
import net.atlasauth.atlas.models.JourneysVersionsResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/flows} resource. */
public final class Journeys {

    private final Transport t;

    public Journeys(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/flows} - The sign-in journeys. Scope: {@code actions:read}. */
    public JourneysListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public JourneysListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/flows", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/flows} - Create a sign-in journey (version 1, active unless activate is false). Steps run top to bottom once a sign-in has passed its factors: refusals (deny, require_method, require_mfa), required actions the user must finish first, Action calls that may refuse, session claims and the redirect after sign-in. Action steps must name workflow_step Actions. -&gt; 201. Scope: {@code actions:write}. */
    public JourneysListResponseDataItem create(JourneysCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JourneysListResponseDataItem create(JourneysCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/flows", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/flows/{id}} - One journey with its active definition (else the latest). Scope: {@code actions:read}. */
    public JourneysListResponseDataItem get(String id) {
        return t.request("GET", "/v1/flows/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/flows/{id}} - Change a journey. A definition becomes the next version (immutable), active unless activate is false. Scope: {@code actions:write}. */
    public JourneysListResponseDataItem update(String id, JourneysUpdateBody body) {
        return t.request("PATCH", "/v1/flows/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/flows/{id}} - Delete a journey and its versions. Scope: {@code actions:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/flows/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/flows/{id}/versions} - Every version of a journey, newest first. Scope: {@code actions:read}. */
    public JourneysVersionsResponse versions(String id) {
        return versions(id, null, null);
    }

    /** As above, with every optional parameter. */
    public JourneysVersionsResponse versions(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/flows/" + enc(id) + "/versions", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/flows/{id}/activate} - Make a version the active one (roll forward or back). Scope: {@code actions:write}. */
    public JourneysListResponseDataItem activate(String id, JourneysActivateBody body) {
        return activate(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JourneysListResponseDataItem activate(String id, JourneysActivateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/flows/" + enc(id) + "/activate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/flows/{id}/simulate} - What-if: run a version (default the active one) for a user and a described sign-in (method, mfa_verified, ip, application_id) without changing anything. Returns the outcome (allowed, held or refused), the effects and a per-step trace. Scope: {@code actions:read}. */
    public JourneysSimulateResponse simulate(String id, JourneysSimulateBody body) {
        return simulate(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JourneysSimulateResponse simulate(String id, JourneysSimulateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/flows/" + enc(id) + "/simulate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
