package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.http.Transport;

import static net.atlasauth.atlas.http.Transport.enc;

/** Security advisories for the Atlas SDKs and platform: the instance's own list and the public catalog. */
public final class SecurityAdvisories {
    private final Transport t;

    public SecurityAdvisories(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/security_advisories} — The advisories that apply to this instance. */
    public JsonNode list() {
        return t.get("/v1/security_advisories", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/security_advisories/{id}/acknowledge} — Acknowledge an advisory. */
    public JsonNode acknowledge(String id) {
        return t.post("/v1/security_advisories/" + enc(id) + "/acknowledge", null, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/public/security_advisories} — The public advisory catalog (no key needed). */
    public JsonNode catalog() {
        return t.get("/v1/public/security_advisories", new TypeReference<>() {
        });
    }
}
