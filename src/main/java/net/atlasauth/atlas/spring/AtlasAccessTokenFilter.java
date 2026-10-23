package net.atlasauth.atlas.spring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Protects an API with an Atlas-issued OAuth access token (an {@code Authorization: Bearer}
 * header, never a cookie). A refusal answers with the verifier's status and RFC 6750 / RFC 9728
 * {@code WWW-Authenticate} challenge; on success the {@link AccessTokenVerifier.Result} is on the
 * request as the {@value #ATTRIBUTE} attribute. Register it on the paths it should guard (for
 * example with a {@code FilterRegistrationBean}).
 */
public class AtlasAccessTokenFilter extends OncePerRequestFilter {

    /** The request attribute holding the verified {@link AccessTokenVerifier.Result}. */
    public static final String ATTRIBUTE = "atlas.accessToken";

    private final AccessTokenVerifier verifier;
    private final String[] requiredScopes;

    public AtlasAccessTokenFilter(AccessTokenVerifier verifier, String... requiredScopes) {
        this.verifier = verifier;
        this.requiredScopes = requiredScopes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        AccessTokenVerifier.Result result = verifier.verify(request.getHeader("Authorization"), requiredScopes);
        if (!result.ok()) {
            response.setStatus(result.status());
            response.setHeader("WWW-Authenticate", result.wwwAuthenticate());
            response.setContentType("application/json");
            String error = result.error() == null ? "unauthorized" : result.error();
            response.getOutputStream().write(("{\"error\":\"" + error + "\"}").getBytes(StandardCharsets.UTF_8));
            return;
        }
        request.setAttribute(ATTRIBUTE, result);
        chain.doFilter(request, response);
    }
}
