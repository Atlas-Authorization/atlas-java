package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateJwtTemplateBody;
import net.atlasauth.atlas.models.JwtTemplate;
import net.atlasauth.atlas.models.ListPage;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/jwt_templates} resource. Templates are keyed by name. */
public final class JwtTemplates {

    private final Transport t;

    public JwtTemplates(Transport t) {
        this.t = t;
    }

    public ListPage<JwtTemplate> list() {
        return t.get("/v1/jwt_templates", new TypeReference<>() {
        });
    }

    /** Fetched by template name. */
    public JwtTemplate get(String name) {
        return t.get("/v1/jwt_templates/" + enc(name), new TypeReference<>() {
        });
    }

    public JwtTemplate create(CreateJwtTemplateBody body) {
        return t.post("/v1/jwt_templates", body, new TypeReference<>() {
        });
    }

    /** Only the claims are updatable; the name is the key. */
    public JwtTemplate update(String name, Map<String, String> claims) {
        Map<String, Object> body = Params.of("claims", claims).map();
        return t.patch("/v1/jwt_templates/" + enc(name), body, new TypeReference<>() {
        });
    }

    /** Returns {@code {object, name, deleted}}. */
    public JsonNode delete(String name) {
        return t.delete("/v1/jwt_templates/" + enc(name), new TypeReference<>() {
        });
    }
}
