package net.atlasauth.atlas.spring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.atlasauth.atlas.betterauth.BridgeDecision;
import net.atlasauth.atlas.betterauth.SessionBridge;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 *
 * <p>With a Better Auth {@link SessionBridge} ({@code atlas.better-auth-bridge.enabled=true}) a GET or HEAD
 * carrying a Better Auth session cookie, and no Atlas session, is first exchanged once with Atlas: signed in,
 * the filter answers {@code 303} to the same URL with the Atlas cookies set and the Better Auth cookies
 * cleared; when a second factor is owed it redirects to {@code atlas.better-auth-bridge.sign-in-url}; a final
 * refusal clears the Better Auth cookies and the request continues unauthenticated. The end user's IP is
 * {@link HttpServletRequest#getRemoteAddr()} (configure {@code server.forward-headers-strategy} behind a proxy),
 * falling back to the first {@code X-Forwarded-For} hop.
 */
public class AtlasAuthenticationFilter extends OncePerRequestFilter {

    private static final boolean SECURITY_PRESENT = ClassUtils.isPresent(
            "org.springframework.security.core.context.SecurityContextHolder",
            AtlasAuthenticationFilter.class.getClassLoader());

    private final SessionVerifier verifier;
    private final String cookieName;
    private final List<String> protectedPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SessionBridge betterAuthBridge;
    private final String betterAuthSignInUrl;

    public AtlasAuthenticationFilter(SessionVerifier verifier, AtlasProperties properties) {
        this(verifier, properties, null);
    }

    /** With the Better Auth session bridge ({@code betterAuthBridge} may be null). */
    public AtlasAuthenticationFilter(SessionVerifier verifier, AtlasProperties properties, SessionBridge betterAuthBridge) {
        this.verifier = verifier;
        this.cookieName = properties.getCookieName();
        this.protectedPaths = properties.getProtectedPaths();
        this.betterAuthBridge = betterAuthBridge;
        this.betterAuthSignInUrl = properties.getBetterAuthBridge().getSignInUrl();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (betterAuthBridge != null && bridge(request, response)) {
            return;
        }
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

    /** Run the Better Auth bridge; true when it answered the request (a redirect). */
    private boolean bridge(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();
        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
            return false;
        }
        Map<String, String> cookies = new LinkedHashMap<>();
        Cookie[] all = request.getCookies();
        if (all != null) {
            for (Cookie c : all) {
                cookies.putIfAbsent(c.getName(), c.getValue());
            }
        }
        BridgeDecision decision = betterAuthBridge.decide(cookies, clientIp(request), request.getHeader("User-Agent"));
        String location = null;
        if (decision.action() == BridgeDecision.Action.SIGNED_IN) {
            String query = request.getQueryString();
            location = request.getRequestURI() + (query != null && !query.isEmpty() ? "?" + query : "");
        } else if (decision.action() == BridgeDecision.Action.NEEDS_MFA
                && betterAuthSignInUrl != null && !betterAuthSignInUrl.isEmpty()) {
            location = decision.continuationUrl(betterAuthSignInUrl);
        }
        for (String header : decision.setCookieHeaders()) {
            response.addHeader("Set-Cookie", header);
        }
        if (location == null) {
            return false;
        }
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader("Location", location);
        response.setHeader("Cache-Control", "no-store");
        return true;
    }

    private static String clientIp(HttpServletRequest request) {
        String remote = request.getRemoteAddr();
        if (remote != null && !remote.isEmpty()) {
            return remote;
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return null;
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
