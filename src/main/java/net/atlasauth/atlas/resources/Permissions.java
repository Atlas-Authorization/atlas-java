package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreatePermissionBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.Permission;
import net.atlasauth.atlas.models.UpdatePermissionBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/permissions} resource. */
public final class Permissions {

    private final Transport t;

    public Permissions(Transport t) {
        this.t = t;
    }

    public ListPage<Permission> list() {
        return t.get("/v1/permissions", new TypeReference<>() {
        });
    }

    public Permission create(CreatePermissionBody body) {
        return create(body, null);
    }

    public Permission create(CreatePermissionBody body, String idempotencyKey) {
        return t.post("/v1/permissions", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** Relabel a custom permission (name/description only — the key is immutable). */
    public Permission update(String id, UpdatePermissionBody body) {
        return t.patch("/v1/permissions/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** Delete a custom permission. Fails with {@code role_in_use} if a role still grants it. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/permissions/" + enc(id), new TypeReference<>() {
        });
    }
}
