package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PATCH /v1/jwt_templates/{name}}: claims and/or settings; the name is the key and cannot change. */
public final class UpdateJwtTemplateBody {
    public Map<String, String> claims;
    public JwtTemplateSettings settings;

    public UpdateJwtTemplateBody claims(Map<String, String> v) { this.claims = v; return this; }
    public UpdateJwtTemplateBody settings(JwtTemplateSettings v) { this.settings = v; return this; }
}
