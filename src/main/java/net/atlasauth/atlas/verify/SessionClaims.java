package net.atlasauth.atlas.verify;

import java.util.List;
import java.util.Map;

/**
 * The verified claims of an Atlas session JWT (&sect;7.3). Well-known claims are
 * typed accessors; any additional claim is reachable through {@link #get(String)}
 * and {@link #raw()}.
 */
public final class SessionClaims {

    private final Map<String, Object> claims;

    public SessionClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    public Object get(String claim) {
        return claims.get(claim);
    }

    /** The full claim set, exactly as it appeared in the token payload. */
    public Map<String, Object> raw() {
        return claims;
    }

    public String iss() {
        return str("iss");
    }

    /** Subject: the user id. */
    public String sub() {
        return str("sub");
    }

    /** Session id. */
    public String sid() {
        return str("sid");
    }

    public Long exp() {
        return num("exp");
    }

    public Long nbf() {
        return num("nbf");
    }

    public Long iat() {
        return num("iat");
    }

    /** Authorized party. */
    public String azp() {
        return str("azp");
    }

    public boolean mfa() {
        return Boolean.TRUE.equals(claims.get("mfa"));
    }

    public String orgId() {
        return str("org_id");
    }

    public String orgSlug() {
        return str("org_slug");
    }

    public String orgRole() {
        return str("org_role");
    }

    @SuppressWarnings("unchecked")
    public List<String> orgPermissions() {
        Object v = claims.get("org_permissions");
        return v instanceof List<?> list ? (List<String>) list : List.of();
    }

    /** The {@code app_roles} claim: the user's instance-wide app roles. */
    @SuppressWarnings("unchecked")
    public List<String> appRoles() {
        Object v = claims.get("app_roles");
        return v instanceof List<?> list ? (List<String>) list : List.of();
    }

    /** The {@code app_permissions} claim: the union of the user's app roles' permissions. */
    @SuppressWarnings("unchecked")
    public List<String> appPermissions() {
        Object v = claims.get("app_permissions");
        return v instanceof List<?> list ? (List<String>) list : List.of();
    }

    /** Whether the user holds the app role (no active organization needed). */
    public boolean hasAppRole(String role) {
        return appRoles().contains(role);
    }

    /** Whether the user's app roles grant the permission (no active organization needed). */
    public boolean hasAppPermission(String permission) {
        return appPermissions().contains(permission);
    }

    private String str(String key) {
        Object v = claims.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private Long num(String key) {
        Object v = claims.get(key);
        return v instanceof Number n ? n.longValue() : null;
    }
}
