package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code POST /v1/jwt_templates}. Both {@code name} and {@code claims}
 * are required. Fluent setters return {@code this}.
 */
public final class CreateJwtTemplateBody {
    public String name;
    public Map<String, String> claims;

    public CreateJwtTemplateBody() {
    }

    public CreateJwtTemplateBody(String name, Map<String, String> claims) {
        this.name = name;
        this.claims = claims;
    }

    public CreateJwtTemplateBody name(String v) { this.name = v; return this; }
    public CreateJwtTemplateBody claims(Map<String, String> v) { this.claims = v; return this; }
}
