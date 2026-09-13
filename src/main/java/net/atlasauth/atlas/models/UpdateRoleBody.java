package net.atlasauth.atlas.models;

/** Body for {@code PATCH /v1/roles/:id}. The key is immutable. */
public final class UpdateRoleBody {
    public String name;
    public String description;
    public String key;

    public UpdateRoleBody name(String v) { this.name = v; return this; }
    public UpdateRoleBody description(String v) { this.description = v; return this; }
    public UpdateRoleBody key(String v) { this.key = v; return this; }
}
