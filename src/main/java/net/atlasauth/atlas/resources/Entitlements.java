package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

/** {@code GET /v1/entitlements} — the effective entitlements (plan features, limits and overrides) of a user or organization. */
public final class Entitlements {
    private final Transport t;

    public Entitlements(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/entitlements?subject_type=&subject_id=} — {@code subjectType} is {@code user} or {@code org}. */
    public JsonNode get(String subjectType, String subjectId) {
        return t.get("/v1/entitlements", Params.of("subject_type", subjectType).set("subject_id", subjectId).map(), new TypeReference<>() {
        });
    }
}
