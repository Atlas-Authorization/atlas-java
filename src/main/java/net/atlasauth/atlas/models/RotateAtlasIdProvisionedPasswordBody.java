package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/platform/atlas_id_apps/{id}/provisioned_accounts/{account}/password}. The password is never printed. */
public final class RotateAtlasIdProvisionedPasswordBody extends ExtensibleBody {
    /** At least 12 characters; omit it and Atlas generates one. */
    public String password;

    public RotateAtlasIdProvisionedPasswordBody() {
    }

    public RotateAtlasIdProvisionedPasswordBody password(String v) { this.password = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public RotateAtlasIdProvisionedPasswordBody set(String key, Object value) { putField(key, value); return this; }

    @Override
    public String toString() {
        return "RotateAtlasIdProvisionedPasswordBody[password=" + (password == null ? "unset" : "***") + "]";
    }
}
