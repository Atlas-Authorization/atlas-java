package net.atlasauth.atlas.spring;

import jakarta.servlet.http.HttpServletRequest;
import net.atlasauth.atlas.verify.SessionClaims;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end-ish slice: a {@code DispatcherServlet} serving a tiny
 * {@code @RestController}, with {@link AtlasAuthenticationFilter} in front,
 * exercised through {@link MockMvc}. A valid Bearer token reaches the protected
 * endpoint and the controller reads the subject via {@link AtlasContext};
 * a missing token is rejected with 401 before the controller runs.
 */
class AtlasAuthenticationFilterMvcTest {

    @RestController
    static class MeController {
        @GetMapping("/api/me")
        String me(HttpServletRequest request) {
            SessionClaims claims = AtlasContext.claims(request);
            return claims.sub();
        }
    }

    private JwksTestFixture jwks;
    private MockMvc mvc;

    @BeforeEach
    void setUp() throws Exception {
        jwks = new JwksTestFixture();
        SessionVerifier verifier = SessionVerifier.builder()
                .jwksUrl(jwks.jwksUrl())
                .issuer(JwksTestFixture.ISSUER)
                .build();
        AtlasProperties properties = new AtlasProperties();
        properties.setProtectedPaths(List.of("/api/**"));

        mvc = MockMvcBuilders.standaloneSetup(new MeController())
                .addFilters(new AtlasAuthenticationFilter(verifier, properties))
                .build();
    }

    @AfterEach
    void tearDown() {
        jwks.close();
    }

    @Test
    void validTokenReachesTheControllerAndSeesTheSubject() throws Exception {
        String token = jwks.signSession(Map.of("org_role", "admin"));

        mvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("usr_1"));
    }

    @Test
    void missingTokenIsRejectedWith401() throws Exception {
        mvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenIsRejectedWith401() throws Exception {
        mvc.perform(get("/api/me").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }
}
