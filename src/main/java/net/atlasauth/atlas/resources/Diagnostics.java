package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.InspectRequestParams;
import net.atlasauth.atlas.models.LoginReadiness;
import net.atlasauth.atlas.models.RequestInspection;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Setup diagnostics ({@code /v1/diagnostics}). */
public final class Diagnostics {

    private final Transport t;

    public Diagnostics(Transport t) {
        this.t = t;
    }

    public LoginReadiness loginReadiness() {
        return loginReadiness(null, null);
    }

    /** {@code GET /v1/diagnostics/login_readiness} — End-to-end login-readiness probe for a given `origin` (CORS, cookie domain, email claim, JWKS), reporting the observed effective runtime values with a per-hop pass/fail and a specific fix. */
    public LoginReadiness loginReadiness(String origin, Boolean live) {
        Map<String, Object> q = Params.of().set("origin", origin).set("live", live).map();
        return t.get("/v1/diagnostics/login_readiness", q, new TypeReference<>() {
        });
    }

    public RequestInspection inspectRequest() {
        return inspectRequest(null);
    }

    /** {@code GET /v1/diagnostics/request_inspector} — Explain why a specific browser request would be rejected: CORS origin, CORS method (the DELETE/PATCH class), session-cookie delivery, rate-limit bucket, captcha requirement, and — with an optional `token` — JWT verification against this instance. */
    public RequestInspection inspectRequest(InspectRequestParams params) {
        return t.get("/v1/diagnostics/request_inspector", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }
}
