package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PATCH /v1/actions/:id}. */
public final class UpdateActionBody {
    public String name;
    public String code;
    public Boolean enabled;
    public Map<String, String> secrets;

    public UpdateActionBody() {
    }

    public UpdateActionBody name(String v) { this.name = v; return this; }
    public UpdateActionBody code(String v) { this.code = v; return this; }
    public UpdateActionBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateActionBody secrets(Map<String, String> v) { this.secrets = v; return this; }
}
