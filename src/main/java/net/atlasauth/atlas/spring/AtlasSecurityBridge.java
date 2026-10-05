package net.atlasauth.atlas.spring;

import net.atlasauth.atlas.verify.SessionClaims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Bridges a verified Atlas session into Spring Security's {@code SecurityContext}.
 *
 * <p>Isolated in its own class on purpose: it is the only code that touches
 * {@code org.springframework.security.*}, so the JVM links those classes lazily
 * when {@link #populate} is first called. {@link AtlasAuthenticationFilter} only
 * calls it after a {@code ClassUtils.isPresent} check, which means the Spring
 * Security dependency is genuinely optional — absent it, this class is never
 * loaded and the filter runs on plain spring-web.
 */
final class AtlasSecurityBridge {

    private AtlasSecurityBridge() {
    }

    static void populate(SessionClaims claims, String token) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : claims.orgPermissions()) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        String role = claims.orgRole();
        if (role != null && !role.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(claims.sub(), token, authorities);
        authentication.setDetails(claims);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
