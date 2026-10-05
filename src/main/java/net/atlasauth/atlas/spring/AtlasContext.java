package net.atlasauth.atlas.spring;

import jakarta.servlet.ServletRequest;
import net.atlasauth.atlas.verify.SessionClaims;
import net.atlasauth.atlas.verify.VerifyResult;

/**
 * Reads the authenticated Atlas session the {@link AtlasAuthenticationFilter}
 * stored on the current request. A controller injects the
 * {@link jakarta.servlet.http.HttpServletRequest} and reads the subject with no
 * further wiring:
 *
 * <pre>{@code
 * @GetMapping("/me")
 * String me(HttpServletRequest request) {
 *     SessionClaims claims = AtlasContext.claims(request);
 *     return claims.sub();
 * }
 * }</pre>
 *
 * <p>Deliberately not a Spring Security dependency: it works with plain
 * spring-web. When Spring Security <em>is</em> on the classpath the filter also
 * populates the {@code SecurityContext}, so {@code @AuthenticationPrincipal} and
 * method security keep working too.
 */
public final class AtlasContext {

    /** The request attribute under which the filter stores the {@link VerifyResult}. */
    public static final String ATTRIBUTE = "atlas";

    private AtlasContext() {
    }

    /**
     * The verified result for this request, or {@code null} if the filter did not
     * authenticate it (no token, or an invalid one on an unguarded path).
     */
    public static VerifyResult get(ServletRequest request) {
        Object value = request == null ? null : request.getAttribute(ATTRIBUTE);
        return value instanceof VerifyResult result ? result : null;
    }

    /** The verified claims, or {@code null} when the request is unauthenticated. */
    public static SessionClaims claims(ServletRequest request) {
        VerifyResult result = get(request);
        return result != null && result.ok() ? result.claims() : null;
    }

    /** True when this request carries a verified Atlas session. */
    public static boolean isAuthenticated(ServletRequest request) {
        return claims(request) != null;
    }
}
