package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateRoleBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.Role;
import net.atlasauth.atlas.models.RolePermissionsResult;
import net.atlasauth.atlas.models.UpdateRoleBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/roles} resource. */
public final class Roles {

    private final Transport t;

    public Roles(Transport t) {
        this.t = t;
    }

    public ListPage<Role> list() {
        return t.get("/v1/roles", new TypeReference<>() {
        });
    }

    public Role create(CreateRoleBody body) {
        return create(body, null);
    }

    public Role create(CreateRoleBody body, String idempotencyKey) {
        return t.post("/v1/roles", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public Role update(String id, UpdateRoleBody body) {
        return t.patch("/v1/roles/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** Replace a role's permission set wholesale. */
    public RolePermissionsResult setPermissions(String id, List<String> permissions) {
        Map<String, Object> body = Params.of("permissions", permissions).map();
        return t.put("/v1/roles/" + enc(id) + "/permissions", body, new TypeReference<>() {
        });
    }

    public JsonNode delete(String id) {
        return delete(id, null);
    }

    /**
     * Delete a role. Pass {@code reassignTo} to move every member onto another role
     * first (atomic); without it, deleting a role people still hold fails with
     * {@code role_in_use}.
     */
    public JsonNode delete(String id, String reassignTo) {
        Map<String, Object> q = Params.of().set("reassign_to", reassignTo).map();
        return t.delete("/v1/roles/" + enc(id), q, new TypeReference<>() {
        });
    }
}
