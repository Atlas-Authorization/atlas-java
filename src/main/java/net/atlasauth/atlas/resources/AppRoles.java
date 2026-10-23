package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The {@code /v1/app_roles} resource — instance-wide user roles, not tied to any organization. A
 * user's app roles ride on the session token as {@code app_roles} / {@code app_permissions}; check
 * them offline with {@code SessionClaims.hasAppRole} / {@code hasAppPermission}.
 */
public final class AppRoles {

    private final Transport t;

    public AppRoles(Transport t) {
        this.t = t;
    }

    public JsonNode list() {
        return t.get("/v1/app_roles", new TypeReference<>() {
        });
    }

    /** {@code key}: 2-64 lowercase letters, digits, _ or -, starting with a letter. */
    public JsonNode create(String key, String name, String description, List<String> permissions) {
        Map<String, Object> body = Params.of("key", key).set("name", name).set("description", description)
                .set("permissions", permissions).map();
        return t.post("/v1/app_roles", body, new TypeReference<>() {
        });
    }

    /** By id or key. */
    public JsonNode get(String idOrKey) {
        return t.get("/v1/app_roles/" + enc(idOrKey), new TypeReference<>() {
        });
    }

    public JsonNode update(String idOrKey, String name, String description, List<String> permissions) {
        Map<String, Object> body = Params.of("name", name).set("description", description)
                .set("permissions", permissions).map();
        return t.patch("/v1/app_roles/" + enc(idOrKey), body, new TypeReference<>() {
        });
    }

    public JsonNode delete(String idOrKey) {
        return t.delete("/v1/app_roles/" + enc(idOrKey), new TypeReference<>() {
        });
    }

    /** The users holding a role, oldest assignment first. */
    public JsonNode listUsers(String idOrKey, Integer limit, Integer offset) {
        return t.get("/v1/app_roles/" + enc(idOrKey) + "/users", Params.of("limit", limit).set("offset", offset).map(),
                new TypeReference<>() {
                });
    }

    /** A user's app roles and effective permissions. */
    public JsonNode getForUser(String userId) {
        return t.get("/v1/users/" + enc(userId) + "/app_roles", new TypeReference<>() {
        });
    }

    /** Replace the user's app roles with {@code roles} (keys or ids). */
    public JsonNode setForUser(String userId, List<String> roles) {
        return t.post("/v1/users/" + enc(userId) + "/app_roles", Params.of("roles", roles).map(),
                new TypeReference<>() {
                });
    }

    /** Add and/or remove app roles (keys or ids); either list may be null. */
    public JsonNode changeForUser(String userId, List<String> add, List<String> remove) {
        return t.post("/v1/users/" + enc(userId) + "/app_roles", Params.of("add", add).set("remove", remove).map(),
                new TypeReference<>() {
                });
    }

    public JsonNode removeFromUser(String userId, String idOrKey) {
        return t.delete("/v1/users/" + enc(userId) + "/app_roles/" + enc(idOrKey), new TypeReference<>() {
        });
    }
}
