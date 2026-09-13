package net.atlasauth.atlas.models;

/** Body for {@code PATCH /v1/permissions/:id}. The key is immutable. */
public final class UpdatePermissionBody {
    public String name;
    public String description;
    public String key;

    public UpdatePermissionBody name(String v) { this.name = v; return this; }
    public UpdatePermissionBody description(String v) { this.description = v; return this; }
    public UpdatePermissionBody key(String v) { this.key = v; return this; }
}
