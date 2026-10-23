package net.atlasauth.atlas.verify;

import java.util.List;

/**
 * The shared authorization primitive — the Java port of {@code @atlas/authz}'s
 * {@code hasFromClaims} / {@code evaluate}. Reads {@code org_role} and
 * {@code org_permissions} straight off the verified claims (no network call),
 * which is the whole point of putting them in the token: a permission change
 * takes effect within one token lifetime, the same bound as revocation.
 */
public final class Authz {

    private Authz() {
    }

    /** True when the claims satisfy the condition (an empty condition = "signed in"). */
    public static boolean has(SessionClaims claims, ProtectCondition condition) {
        return evaluate(claims, condition) == null;
    }

    /**
     * Evaluate a condition, returning {@code null} when allowed or the failure
     * reason ({@code role} / {@code permission}) otherwise.
     */
    public static String evaluate(SessionClaims claims, ProtectCondition condition) {
        if (condition == null || condition.isEmpty()) {
            return null; // "signed in" — any valid session passes.
        }
        List<String> permissions = claims.orgPermissions();
        String role = claims.orgRole();

        if (condition.role != null && !condition.role.equals(role)) {
            return "role";
        }
        if (condition.permission != null && !permissions.contains(condition.permission)) {
            return "permission";
        }
        if (condition.anyPermission != null && !condition.anyPermission.isEmpty()
                && condition.anyPermission.stream().noneMatch(permissions::contains)) {
            return "permission";
        }
        if (condition.allPermissions != null && !condition.allPermissions.isEmpty()
                && !permissions.containsAll(condition.allPermissions)) {
            return "permission";
        }
        if (condition.appRole != null && !claims.hasAppRole(condition.appRole)) {
            return "role";
        }
        if (condition.appPermission != null && !claims.hasAppPermission(condition.appPermission)) {
            return "permission";
        }
        return null;
    }

    public static SessionClaims protect(SessionClaims claims, ProtectCondition condition) {
        String reason = evaluate(claims, condition);
        if (reason != null) {
            throw new ForbiddenError(reason, condition);
        }
        return claims;
    }
}
