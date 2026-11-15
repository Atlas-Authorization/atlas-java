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

    /** {@code sso_connection_id}: the enterprise SSO connection the session was made through, or null. */
    public String ssoConnectionId() {
        return str("sso_connection_id");
    }

    /**
     * {@code sso_organization_id}: the Atlas organization that owns the SSO connection, or null for an instance-wide
     * connection. Independent of {@link #orgId()}: scope a per-organization "require SSO" rule on this, never on
     * "signed in via SSO".
     */
    public String ssoOrganizationId() {
        return str("sso_organization_id");
    }

    /** {@code sso_connection_type}: {@code saml} or {@code oidc}, or null. */
    public String ssoConnectionType() {
        String v = str("sso_connection_type");
        return "saml".equals(v) || "oidc".equals(v) ? v : null;
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

    /**
     * The {@code pol} claim: the accepted version of every required policy, keyed by policy key
     * (for example {@code {"tos": "2026-10-24"}}). Present only when the instance turned on the
     * policy session claim; empty otherwise.
     */
    public Map<String, String> pol() {
        Object v = claims.get("pol");
        if (!(v instanceof Map<?, ?> m)) {
            return Map.of();
        }
        Map<String, String> out = new java.util.LinkedHashMap<>();
        m.forEach((k, val) -> out.put(String.valueOf(k), String.valueOf(val)));
        return out;
    }

    /** Whether the token records acceptance of {@code version} of the policy {@code key}. */
    public boolean hasAcceptedPolicy(String key, String version) {
        return version != null && version.equals(pol().get(key));
    }

    /** {@code auth_time}: epoch seconds of the first-factor sign-in or the last step-up, or null. */
    public Long authTime() {
        return num("auth_time");
    }

    /** {@code acr}: the authentication context ({@code urn:atlas:acr:basic}, {@code :mfa}, {@code :step_up}), or null. */
    public String acr() {
        return str("acr");
    }

    /** {@code amr}: the authentication methods used. */
    @SuppressWarnings("unchecked")
    public List<String> amr() {
        Object v = claims.get("amr");
        return v instanceof List<?> list ? (List<String>) list : List.of();
    }

    /**
     * Whether {@code auth_time} is within {@code maxAgeSeconds} of now: the "recent sign-in" gate for a
     * sensitive action. A token without {@code auth_time} never is.
     */
    public boolean authenticatedWithin(long maxAgeSeconds) {
        return authenticatedWithin(maxAgeSeconds, System.currentTimeMillis());
    }

    /** As {@link #authenticatedWithin(long)}, against an explicit clock (epoch milliseconds). */
    public boolean authenticatedWithin(long maxAgeSeconds, long nowMs) {
        Long at = authTime();
        return at != null && nowMs / 1000.0 - at <= maxAgeSeconds;
    }

    /** Whether the session proved a second factor: {@code acr} is the mfa level, or {@code amr} includes {@code mfa}. */
    public boolean usedMultiFactor() {
        return "urn:atlas:acr:mfa".equals(acr()) || amr().contains("mfa");
    }

    /**
     * Whether the session satisfies a "requires MFA" rule: the server's own {@code mfa_satisfied} verdict when
     * the token carries it, else {@code mfa}, {@link #usedMultiFactor()}, or an enterprise SSO sign-in (the
     * identity provider owns the second factor).
     */
    public boolean mfaSatisfied() {
        if (claims.get("mfa_satisfied") instanceof Boolean b) {
            return b;
        }
        return mfa() || usedMultiFactor() || claims.get("sso_connection_id") instanceof String;
    }

    /**
     * Whether the session meets the active organization's MFA policy: true when the organization does not
     * require MFA or the token says {@code org_mfa_ok}. Pass the organization you are authorizing for (its
     * Atlas id or external id): a token whose active organization is another one answers false.
     */
    public boolean mfaOrgSatisfied(String orgIdOrExternalId) {
        boolean required = Boolean.TRUE.equals(claims.get("org_mfa_required"));
        if (orgIdOrExternalId == null) {
            return !required || Boolean.TRUE.equals(claims.get("org_mfa_ok"));
        }
        boolean matches = orgIdOrExternalId.equals(claims.get("org_id")) || orgIdOrExternalId.equals(claims.get("org_external_id"));
        if (required) {
            return Boolean.TRUE.equals(claims.get("org_mfa_ok")) && (matches || claims.get("org_id") == null);
        }
        return matches;
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
