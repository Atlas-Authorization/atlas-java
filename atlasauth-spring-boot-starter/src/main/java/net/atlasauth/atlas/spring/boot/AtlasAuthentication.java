package net.atlasauth.atlas.spring.boot;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An authenticated Atlas session. Authorities: {@code ROLE_<orgRole>} (organization role), {@code ORG_ROLE_<orgRole>},
 * {@code ROLE_<appRole>} / {@code APP_ROLE_<appRole>} for each app role, and every organization / app permission as
 * {@code PERM_<permission>} and as the bare permission string (so {@code hasAuthority("reports:read")} works).
 */
public final class AtlasAuthentication extends AbstractAuthenticationToken {

    private final AtlasPrincipal principal;
    private final String token;

    public AtlasAuthentication(AtlasPrincipal principal, String token) {
        super(authoritiesOf(principal));
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    static Collection<GrantedAuthority> authoritiesOf(AtlasPrincipal p) {
        Set<String> out = new LinkedHashSet<>();
        if (p.orgRole() != null && !p.orgRole().isEmpty()) {
            out.add("ROLE_" + p.orgRole());
            out.add("ORG_ROLE_" + p.orgRole());
        }
        for (String r : p.appRoles()) {
            out.add("ROLE_" + r);
            out.add("APP_ROLE_" + r);
        }
        for (String perm : p.orgPermissions()) {
            out.add(perm);
            out.add("PERM_" + perm);
        }
        for (String perm : p.appPermissions()) {
            out.add(perm);
            out.add("PERM_" + perm);
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        out.forEach(a -> authorities.add(new SimpleGrantedAuthority(a)));
        return authorities;
    }

    @Override
    public AtlasPrincipal getPrincipal() {
        return principal;
    }

    /** The verified token. */
    @Override
    public String getCredentials() {
        return token;
    }
}
