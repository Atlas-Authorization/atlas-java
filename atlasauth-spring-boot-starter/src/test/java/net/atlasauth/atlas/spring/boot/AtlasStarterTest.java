package net.atlasauth.atlas.spring.boot;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.http.HttpServletResponse;
import net.atlasauth.atlas.rp.RelyingParty;
import net.atlasauth.atlas.spring.AtlasAutoConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class AtlasStarterTest {

    private static final String ISSUER = "https://clerk.example.com";
    private static HttpServer server;
    private static RSAKey key;
    private static String jwksUrl;

    @BeforeAll
    static void start() throws Exception {
        key = new RSAKeyGenerator(2048).keyID("starter-key").generate();
        String jwks = new JWKSet(key.toPublicJWK()).toString();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/jwks", ex -> {
            byte[] b = jwks.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("content-type", "application/json");
            ex.sendResponseHeaders(200, b.length);
            ex.getResponseBody().write(b);
            ex.close();
        });
        server.start();
        jwksUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/jwks";
    }

    @AfterAll
    static void stop() {
        server.stop(0);
    }

    private static String token() throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(ISSUER).subject("usr_1").claim("sid", "sess_1").claim("org_id", "org_1")
                .claim("org_role", "admin").claim("org_permissions", java.util.List.of("reports:read")).claim("app_roles", java.util.List.of("staff"))
                .issueTime(new Date()).expirationTime(new Date(System.currentTimeMillis() + 60_000)).build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("starter-key").build(), claims);
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class, AtlasAutoConfiguration.class, AtlasStarterAutoConfiguration.class))
            .withPropertyValues("atlas.issuer=" + ISSUER, "atlas.jwks-url=" + jwksUrl);

    @Test
    void filterAuthenticatesAValidTokenNetworklessIntoAnAtlasPrincipal() {
        runner.run(ctx -> {
            AtlasSecurityFilter filter = ctx.getBean(AtlasSecurityFilter.class);
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/x");
            req.addHeader("Authorization", "Bearer " + token());
            AtomicReference<Authentication> seen = new AtomicReference<>();
            filter.doFilter(req, new MockHttpServletResponse(), new MockFilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest r, jakarta.servlet.ServletResponse s) {
                    seen.set(SecurityContextHolder.getContext().getAuthentication());
                }
            });
            assertThat(seen.get()).isInstanceOf(AtlasAuthentication.class);
            AtlasPrincipal p = ((AtlasAuthentication) seen.get()).getPrincipal();
            assertThat(p.id()).isEqualTo("usr_1");
            assertThat(p.orgId()).isEqualTo("org_1");
            assertThat(p.orgRole()).isEqualTo("admin");
            assertThat(seen.get().getAuthorities()).extracting(Object::toString)
                    .contains("ROLE_admin", "reports:read", "PERM_reports:read", "ROLE_staff");
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        });
    }

    @Test
    void anInvalidTokenLeavesTheRequestAnonymous() {
        runner.run(ctx -> {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/x");
            req.addHeader("Authorization", "Bearer not-a-jwt");
            AtomicReference<Authentication> seen = new AtomicReference<>();
            ctx.getBean(AtlasSecurityFilter.class).doFilter(req, new MockHttpServletResponse(), new MockFilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest r, jakarta.servlet.ServletResponse s) {
                    seen.set(SecurityContextHolder.getContext().getAuthentication());
                }
            });
            assertThat(seen.get()).isNull();
        });
    }

    @Test
    void loginFlowBeansAppearOnlyWithAClientId() {
        runner.run(ctx -> assertThat(ctx).doesNotHaveBean(RelyingParty.class));
        runner.withPropertyValues("atlas.login.client-id=oc_1", "atlas.login.redirect-uri=https://app.example.com/auth/callback",
                        "atlas.login.secret=0123456789012345678901234567890123456789")
                .run(ctx -> assertThat(ctx).hasSingleBean(RelyingParty.class));
    }
}
