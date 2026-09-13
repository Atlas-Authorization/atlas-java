package net.atlasauth.atlas.verify;

import java.util.List;

/**
 * An authorization condition, the server-side twin of the {@code <Protect>}
 * contract the Atlas React/Next.js SDKs evaluate. An empty condition means
 * simply "signed in". Fields combine with AND; {@code anyPermission} is an OR set
 * and {@code allPermissions} an AND set.
 *
 * <pre>{@code
 * new ProtectCondition().permission("org:billing:manage");
 * new ProtectCondition().role("admin");
 * new ProtectCondition().anyPermission(List.of("a", "b"));
 * }</pre>
 */
public final class ProtectCondition {
    public String role;
    public String permission;
    public List<String> anyPermission;
    public List<String> allPermissions;

    public static ProtectCondition none() {
        return new ProtectCondition();
    }

    public ProtectCondition role(String role) {
        this.role = role;
        return this;
    }

    public ProtectCondition permission(String permission) {
        this.permission = permission;
        return this;
    }

    public ProtectCondition anyPermission(List<String> anyPermission) {
        this.anyPermission = anyPermission;
        return this;
    }

    public ProtectCondition allPermissions(List<String> allPermissions) {
        this.allPermissions = allPermissions;
        return this;
    }

    public boolean isEmpty() {
        return role == null && permission == null
                && (anyPermission == null || anyPermission.isEmpty())
                && (allPermissions == null || allPermissions.isEmpty());
    }
}
