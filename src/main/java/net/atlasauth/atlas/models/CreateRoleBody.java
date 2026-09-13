package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/roles}. */
public final class CreateRoleBody {
    public String key;
    public String name;
    public String description;
    public List<String> permissions;

    public CreateRoleBody() {
    }

    public CreateRoleBody(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public CreateRoleBody key(String v) { this.key = v; return this; }
    public CreateRoleBody name(String v) { this.name = v; return this; }
    public CreateRoleBody description(String v) { this.description = v; return this; }
    public CreateRoleBody permissions(List<String> v) { this.permissions = v; return this; }
}
