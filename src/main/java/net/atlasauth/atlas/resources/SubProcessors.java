package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.http.Transport;

/** {@code GET /v1/instance/sub_processors} — the third parties that process this instance's data, for your own compliance records. */
public final class SubProcessors {
    private final Transport t;

    public SubProcessors(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/instance/sub_processors} — The sub-processors in use, derived from what the instance has configured. */
    public JsonNode get() {
        return t.get("/v1/instance/sub_processors", new TypeReference<>() {
        });
    }
}
