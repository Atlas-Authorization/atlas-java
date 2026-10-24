package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/integrations/*} — the routes an integrated service calls with the instance secret key (or the
 * partner link the instance owner granted it): report a token it refused, read the agent-sidebar card for a
 * user, run a card action, read an instance's public metadata. Connect integrations with
 * {@link Instance#setIntegration}.
 */
public final class Integrations {
    private final Transport t;

    public Integrations(Transport t) {
        this.t = t;
    }

    /**
     * {@code POST /v1/integrations/verify_errors} — Record a token an integrated service refused.
     * {@code error} is aud_mismatch, expired, bad_signature, unknown_kid, session_revoked, invalid_claims or other.
     */
    public JsonNode reportVerifyError(String integration, String error) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("integration", integration);
        body.put("error", error);
        return reportVerifyError(body);
    }

    /** {@code POST /v1/integrations/verify_errors} — with the optional {@code audience}, {@code at}, {@code kid}, {@code sid} and {@code detail}. */
    public JsonNode reportVerifyError(Map<String, Object> body) {
        return t.post("/v1/integrations/verify_errors", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/integrations/{integration}/card} — The agent-sidebar card for a user (by token {@code sub}, and {@code iss} when the user is not on this instance's issuer). */
    public JsonNode card(String integration, String sub, String iss) {
        return t.get("/v1/integrations/" + enc(integration) + "/card", Params.of("sub", sub).set("iss", iss).map(), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/integrations/{integration}/card/actions/revoke_sessions} — Sign the user out everywhere, audited with the agent ({@code id}, {@code name}, {@code email}; may be null). */
    public JsonNode revokeSessions(String integration, String sub, String iss, Map<String, Object> agent) {
        Map<String, Object> body = Params.of("sub", sub).set("iss", iss).set("agent", agent).map();
        return t.post("/v1/integrations/" + enc(integration) + "/card/actions/revoke_sessions", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/public/instances/{id}} — The public metadata of an instance: issuer, JWKS, token schema and its integrations (no key needed). */
    public JsonNode instanceMetadata(String instanceId) {
        return t.get("/v1/public/instances/" + enc(instanceId), new TypeReference<>() {
        });
    }
}
