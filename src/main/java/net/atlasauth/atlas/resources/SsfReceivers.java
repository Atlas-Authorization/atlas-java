package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.SsfReceiversCreateBody;
import net.atlasauth.atlas.models.SsfReceiversListResponse;
import net.atlasauth.atlas.models.SsfReceiversUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/ssf} resource. */
public final class SsfReceivers {

    private final Transport t;

    public SsfReceivers(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/ssf/receivers} - List inbound Shared Signals receivers: transmitters whose signed events act on this instance's users. Scope: {@code ssf:read}. */
    public SsfReceiversListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public SsfReceiversListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/ssf/receivers", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/ssf/receivers} - Register a transmitter. Its SETs are POSTed (`application/secevent+jwt`) to the returned `receive_endpoint` and verified against `jwks_uri`, `issuer` and the optional `audience`. `actions` maps an event type URI to `revoke_sessions`, `refresh_claims` (invalidate minted tokens so the next refresh carries fresh claims, sessions stay), `ban` or `ignore`; unmapped events are ignored. The default maps session-revoked, credential-change, credential-compromise and device-compliance-change (only when the new status is not-compliant) to `revoke_sessions`, token-claims-change to `refresh_claims` and account-disabled to `ban`. Scope: {@code ssf:write}. */
    public JsonNode create(SsfReceiversCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode create(SsfReceiversCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/ssf/receivers", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code PATCH /v1/ssf/receivers/{id}} - Change a receiver: name, JWKS URI, audience, action mapping, or enabled. Scope: {@code ssf:write}. */
    public JsonNode update(String id, SsfReceiversUpdateBody body) {
        return t.request("PATCH", "/v1/ssf/receivers/" + enc(id), null, body, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code DELETE /v1/ssf/receivers/{id}} - Delete an inbound SSF receiver; its endpoint stops accepting events. Scope: {@code ssf:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/ssf/receivers/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
