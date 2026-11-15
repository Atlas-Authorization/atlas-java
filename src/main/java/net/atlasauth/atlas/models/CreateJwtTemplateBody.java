package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code POST /v1/jwt_templates}. Both {@code name} and {@code claims}
 * are required. Fluent setters return {@code this}.
 */
public final class CreateJwtTemplateBody {
    public String name;
    public Map<String, String> claims;
    /** Start from a preset (supabase, hasura, organization_membership, chatly-visitor, firebase); claims and settings you send win. */
    public String preset;
    /** Per-template mint settings; omit to keep the stored ones. */
    public JwtTemplateSettings settings;

    public CreateJwtTemplateBody() {
    }

    public CreateJwtTemplateBody(String name, Map<String, String> claims) {
        this.name = name;
        this.claims = claims;
    }

    public CreateJwtTemplateBody name(String v) { this.name = v; return this; }
    public CreateJwtTemplateBody claims(Map<String, String> v) { this.claims = v; return this; }
    public CreateJwtTemplateBody preset(String v) { this.preset = v; return this; }
    public CreateJwtTemplateBody settings(JwtTemplateSettings v) { this.settings = v; return this; }
}
