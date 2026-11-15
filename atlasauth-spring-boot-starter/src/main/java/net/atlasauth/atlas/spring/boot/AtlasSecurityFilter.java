package net.atlasauth.atlas.spring.boot;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.atlasauth.atlas.rp.RelyingParty;
import net.atlasauth.atlas.rp.RelyingPartyFilter;
import net.atlasauth.atlas.verify.SessionClaims;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The resource-server filter: verifies the Atlas session token on each request with {@link SessionVerifier}
 * (signature against cached JWKS, issuer, expiry: no network call on the request path) and, when valid, puts an
 * {@link AtlasAuthentication} in the {@code SecurityContext}. A missing or invalid token leaves the context
 * anonymous, so Spring Security's own rules decide (401 via the entry point). The token comes from
 * {@code Authorization: Bearer} first, then the session cookie.
 */
public class AtlasSecurityFilter extends OncePerRequestFilter {

    private final SessionVerifier verifier;
    private final String cookieName;

    public AtlasSecurityFilter(SessionVerifier verifier, String cookieName) {
        this.verifier = verifier;
        this.cookieName = cookieName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = extract(request);
        if (token != null) {
            VerifyResult result = verifier.verify(token);
            if (result.ok()) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(new AtlasAuthentication(new AtlasPrincipal(result.claims()), token));
                SecurityContextHolder.setContext(context);
            }
        }
        RelyingParty.User signedIn = RelyingPartyFilter.user(request);
        if (signedIn != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // the hosted-redirect sign-in (atlas.login.*) already authenticated this request
            Map<String, Object> c = new LinkedHashMap<>();
            c.put("sub", signedIn.sub());
            c.put("sid", signedIn.sid());
            c.put("email", signedIn.email());
            c.put("org_id", signedIn.orgId());
            c.put("org_role", signedIn.orgRole());
            c.put("auth_time", signedIn.authTime());
            c.values().removeIf(java.util.Objects::isNull);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new AtlasAuthentication(new AtlasPrincipal(new SessionClaims(c)), ""));
            SecurityContextHolder.setContext(context);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String extract(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authorization.substring(7).trim();
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookieName != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
