package net.atlasauth.atlas.spring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Servlet filter that turns the SDK's {@link SessionVerifier} into Spring-native
 * request authentication. It verifies whatever a request carries — the
 * {@code Authorization: Bearer} header wins over the session cookie — and on
 * success stashes the {@link VerifyResult} as the {@code atlas} request attribute
 * (see {@link AtlasContext}).
 *
 * <p>All verification is local, against cached JWKS; the filter adds no network
 * call on the hot path. It extends {@link OncePerRequestFilter} from spring-web
 * and does not require Spring Security — but when Security is on the classpath it
 * also populates the {@code SecurityContext} via {@link AtlasSecurityBridge}.
 *
 * <p>A request whose path matches {@link AtlasProperties#getProtectedPaths()}
 * without a valid session is answered with {@code 401} and the chain stops.
 * Anything else continues down the chain, authenticated or not.
 */
public class AtlasAuthenticationFilter extends OncePerRequestFilter {

    private static final boolean SECURITY_PRESENT = ClassUtils.isPresent(
            "org.springframework.security.core.context.SecurityContextHolder",
            AtlasAuthenticationFilter.class.getClassLoader());

    private final SessionVerifier verifier;
    private final String cookieName;
    private final List<String> protectedPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AtlasAuthenticationFilter(SessionVerifier verifier, AtlasProperties properties) {
        this.verifier = verifier;
        this.cookieName = properties.getCookieName();
        this.protectedPaths = properties.getProtectedPaths();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = extractToken(request);
        VerifyResult result = token != null ? verifier.verify(token) : null;
        boolean authenticated = result != null && result.ok();

        if (authenticated) {
            request.setAttribute(AtlasContext.ATTRIBUTE, result);
            if (SECURITY_PRESENT) {
                AtlasSecurityBridge.populate(result.claims(), token);
            }
        }

        if (!authenticated && isProtected(request)) {
            unauthorized(response);
            return;
        }

        chain.doFilter(request, response);
    }

    /** {@code Authorization: Bearer <jwt>} first, then the session cookie. */
    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isProtected(HttpServletRequest request) {
        if (protectedPaths.isEmpty()) {
            return false;
        }
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        for (String pattern : protectedPaths) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private static void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getOutputStream().write(
                "{\"error\":\"unauthorized\"}".getBytes(StandardCharsets.UTF_8));
    }
}
