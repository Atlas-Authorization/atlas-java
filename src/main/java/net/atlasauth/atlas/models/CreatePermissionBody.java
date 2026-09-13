package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/permissions}. */
public final class CreatePermissionBody {
    public String key;
    public String name;
    public String description;

    public CreatePermissionBody() {
    }

    public CreatePermissionBody(String key) {
        this.key = key;
    }

    public CreatePermissionBody key(String v) { this.key = v; return this; }
    public CreatePermissionBody name(String v) { this.name = v; return this; }
    public CreatePermissionBody description(String v) { this.description = v; return this; }
}
