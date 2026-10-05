package net.atlasauth.atlas.spring;

import jakarta.servlet.http.Cookie;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives {@link AtlasAuthenticationFilter} directly with Spring's mock servlet
 * objects — no DispatcherServlet — against the real {@link SessionVerifier}.
 */
class AtlasAuthenticationFilterTest {

    private JwksTestFixture jwks;
    private SessionVerifier verifier;

    @BeforeEach
    void setUp() throws Exception {
        jwks = new JwksTestFixture();
        verifier = SessionVerifier.builder()
                .jwksUrl(jwks.jwksUrl())
                .issuer(JwksTestFixture.ISSUER)
                .build();
    }

    @AfterEach
    void tearDown() {
        jwks.close();
    }

    private AtlasAuthenticationFilter filter(AtlasProperties properties) {
        return new AtlasAuthenticationFilter(verifier, properties);
    }

    private static AtlasProperties guarding(String... patterns) {
        AtlasProperties properties = new AtlasProperties();
        properties.setProtectedPaths(List.of(patterns));
        return properties;
    }

    @Test
    void bearerHeaderAuthenticatesAndStoresTheResult() throws Exception {
        String token = jwks.signSession(Map.of("org_role", "admin"));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/me");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter(guarding("/api/**")).doFilter(request, response, chain);

        // Chain proceeded, and the verified session is on the request.
        assertNotNull(chain.getRequest());
        assertEquals(200, response.getStatus());
        VerifyResult result = AtlasContext.get(request);
        assertTrue(result.ok());
        assertEquals("usr_1", AtlasContext.claims(request).sub());
        assertEquals("admin", AtlasContext.claims(request).orgRole());
    }

    @Test
    void sessionCookieAuthenticatesWhenNoBearerHeader() throws Exception {
        String token = jwks.signSession(Map.of());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/me");
        request.setCookies(new Cookie("__session", token));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter(guarding("/api/**")).doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertEquals("usr_1", AtlasContext.claims(request).sub());
    }

    @Test
    void honoursACustomCookieName() throws Exception {
        String token = jwks.signSession(Map.of());
        AtlasProperties properties = guarding("/api/**");
        properties.setCookieName("my_session");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/me");
        request.setCookies(new Cookie("my_session", token));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter(properties).doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertEquals("usr_1", AtlasContext.claims(request).sub());
    }

    @Test
    void missingTokenOnProtectedPathReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/me");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter(guarding("/api/**")).doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        // Chain did NOT proceed.
        assertNull(chain.getRequest());
        assertNull(AtlasContext.get(request));
    }

    @Test
    void invalidTokenOnProtectedPathReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/me");
        request.addHeader("Authorization", "Bearer not-a-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter(guarding("/api/**")).doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertNull(chain.getRequest());
    }

    @Test
    void unguardedPathWithoutTokenProceedsUnauthenticated() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/public/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // No protected paths configured.
        filter(new AtlasProperties()).doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
        assertNull(AtlasContext.get(request));
    }
}
