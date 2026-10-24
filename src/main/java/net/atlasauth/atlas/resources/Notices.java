package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import static net.atlasauth.atlas.http.Transport.enc;

/** {@code /v1/instance/notices} — notices Atlas posts to the instance's operators (deprecations, maintenance, policy changes). */
public final class Notices {
    private final Transport t;

    public Notices(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/instance/notices} — Unacknowledged notices. */
    public JsonNode list() {
        return list(null);
    }

    /** {@code GET /v1/instance/notices?all=} — With {@code all} true, acknowledged notices too. */
    public JsonNode list(Boolean all) {
        return t.get("/v1/instance/notices", Params.of("all", all).map(), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/notices/{id}/acknowledge} — Mark a notice as read. */
    public JsonNode acknowledge(String id) {
        return t.post("/v1/instance/notices/" + enc(id) + "/acknowledge", null, new TypeReference<>() {
        });
    }
}
