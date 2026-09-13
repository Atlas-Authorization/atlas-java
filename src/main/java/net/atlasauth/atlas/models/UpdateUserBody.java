package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PATCH /v1/users/:id}. Unset fields are omitted. */
public final class UpdateUserBody {
    public String firstName;
    public String lastName;
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;

    public UpdateUserBody firstName(String v) { this.firstName = v; return this; }
    public UpdateUserBody lastName(String v) { this.lastName = v; return this; }
    public UpdateUserBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public UpdateUserBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
}
