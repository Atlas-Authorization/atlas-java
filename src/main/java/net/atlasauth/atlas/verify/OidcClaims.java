package net.atlasauth.atlas.verify;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Atlas-specific claims on the ID tokens and userinfo of an OIDC relying party: the organization a sign-in was made in
 * ({@code org_id}, {@code org_slug}, {@code org_role}, {@code org_permissions}), every membership ({@code orgs}, with the
 * {@code organizations} scope) and the enterprise SSO connection ({@code sso_connection_id}, {@code sso_organization_id},
 * {@code sso_connection_type}, {@code sso_tenant_id}, {@code sso_issuer}, {@code sso_domain}). Pass the verified claims.
 */
public final class OidcClaims {
    /** The scope that adds the {@code orgs} membership list to the ID token and userinfo. */
    public static final String SCOPE_ORGANIZATIONS = "organizations";
    /** The authorize parameter that pins the organization (id or slug) a sign-in is made in. */
    public static final String ORGANIZATION_PARAM = "organization";

    private OidcClaims() {
    }

    /** The organization a sign-in was made in. */
    public record Organization(String id, String slug, String role, List<String> permissions) {
    }

    /** One membership from the {@code orgs} claim. */
    public record Membership(String id, String slug, String role) {
    }

    /**
     * The enterprise SSO context. {@code organizationId} is the Atlas organization that owns the connection (null for an
     * instance-wide one), independent of the {@code org_*} claims: scope a per-organization "require SSO" rule on it, never
     * on "signed in via SSO". {@code type} is {@code saml} or {@code oidc} (null when absent).
     */
    public record SsoContext(String connectionId, String organizationId, String type, String tenantId, String issuer,
                             String domain) {
    }

    public static Optional<Organization> organizationOf(Map<String, Object> claims) {
        if (!(claims.get("org_id") instanceof String id)) {
            return Optional.empty();
        }
        List<String> perms = new ArrayList<>();
        if (claims.get("org_permissions") instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof String s) {
                    perms.add(s);
                }
            }
        }
        return Optional.of(new Organization(id, string(claims.get("org_slug"), ""), string(claims.get("org_role"), ""), perms));
    }

    /** Every membership, or empty when the {@code organizations} scope was not requested. */
    public static Optional<List<Membership>> organizationsOf(Map<String, Object> claims) {
        if (!(claims.get("orgs") instanceof List<?> l)) {
            return Optional.empty();
        }
        List<Membership> out = new ArrayList<>();
        for (Object o : l) {
            if (o instanceof Map<?, ?> m && m.get("id") instanceof String id) {
                out.add(new Membership(id, string(m.get("slug"), ""), string(m.get("role"), "")));
            }
        }
        return Optional.of(out);
    }

    public static Optional<SsoContext> ssoOf(Map<String, Object> claims) {
        if (!(claims.get("sso_connection_id") instanceof String id)) {
            return Optional.empty();
        }
        String type = string(claims.get("sso_connection_type"), null);
        return Optional.of(new SsoContext(id, string(claims.get("sso_organization_id"), null),
                "saml".equals(type) || "oidc".equals(type) ? type : null,
                string(claims.get("sso_tenant_id"), null), string(claims.get("sso_issuer"), null),
                string(claims.get("sso_domain"), null)));
    }

    /**
     * Add (or replace) the {@code organization} authorize parameter: the sign-in must be made in that organization (a user
     * who is not a member gets {@code access_denied}) and the token's {@code org_*} claims are that organization's.
     */
    public static String authorizeUrlWithOrganization(String authorizeUrl, String organization) {
        URI uri = URI.create(authorizeUrl);
        StringBuilder q = new StringBuilder();
        if (uri.getRawQuery() != null) {
            for (String pair : uri.getRawQuery().split("&")) {
                if (!pair.isEmpty() && !pair.startsWith(ORGANIZATION_PARAM + "=") && !pair.equals(ORGANIZATION_PARAM)) {
                    q.append(pair).append('&');
                }
            }
        }
        q.append(ORGANIZATION_PARAM).append('=').append(URLEncoder.encode(organization, StandardCharsets.UTF_8));
        String base = authorizeUrl.contains("?") ? authorizeUrl.substring(0, authorizeUrl.indexOf('?')) : authorizeUrl;
        String frag = uri.getRawFragment() != null ? "#" + uri.getRawFragment() : "";
        if (!frag.isEmpty() && base.contains("#")) {
            base = base.substring(0, base.indexOf('#'));
        }
        return base + "?" + q + frag;
    }

    private static String string(Object v, String fallback) {
        return v instanceof String s ? s : fallback;
    }
}
