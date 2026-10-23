package net.atlasauth.atlas.spring;

import jakarta.servlet.http.Cookie;
import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.betterauth.ExchangeResult;
import net.atlasauth.atlas.betterauth.SessionBridge;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Better Auth session bridge in the Spring filter (redirect when signed in, continuation when a second
 * factor is owed, cookie clears on a final refusal, GET/HEAD only) and its auto-configuration.
 */
class BetterAuthBridgeFilterTest {

    private JwksTestFixture jwks;
    private SessionVerifier verifier;
    private final Deque<ExchangeResult> results = new ArrayDeque<>();
    private final List<String> calls = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        jwks = new JwksTestFixture();
        verifier = SessionVerifier.builder().jwksUrl(jwks.jwksUrl()).issuer(JwksTestFixture.ISSUER).build();
    }

    @AfterEach
    void tearDown() {
        jwks.close();
    }

    private AtlasAuthenticationFilter filter(String signInUrl, String... protectedPaths) {
        AtlasProperties properties = new AtlasProperties();
        properties.setProtectedPaths(List.of(protectedPaths));
        properties.getBetterAuthBridge().setEnabled(true);
        properties.getBetterAuthBridge().setSignInUrl(signInUrl);
        SessionBridge bridge = SessionBridge.builder(AtlasClient.of("sk_test_123"))
                .exchanger((c, token, ip, ua) -> {
                    calls.add(token + "|" + ip + "|" + ua);
                    return results.poll();
                }).build();
        return new AtlasAuthenticationFilter(verifier, properties, bridge);
    }

    private static MockHttpServletRequest get(String uri, String query) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setQueryString(query);
        request.setRemoteAddr("198.51.100.3");
        request.addHeader("User-Agent", "UA/3");
        request.setCookies(new Cookie("better-auth.session_token", "tok.sig"));
        return request;
    }

    @Test
    void signedInRedirectsToTheSameUrlWithTheAtlasCookies() throws Exception {
        results.add(ExchangeResult.session("jwt1", "rt1", "s", "u", 60));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter(null).doFilter(get("/dash", "x=1"), response, chain);

        assertEquals(303, response.getStatus());
        assertEquals("/dash?x=1", response.getHeader("Location"));
        assertEquals("no-store", response.getHeader("Cache-Control"));
        assertNull(chain.getRequest(), "the chain does not run");
        List<String> cookies = response.getHeaders("Set-Cookie");
        // MockHttpServletResponse re-serializes Set-Cookie, so compare attributes, not their order.
        assertTrue(cookies.get(0).startsWith("__session=jwt1; Path=/"), cookies.get(0));
        assertTrue(cookies.get(0).contains("Secure") && cookies.get(0).contains("SameSite=Lax")
                && !cookies.get(0).contains("HttpOnly"), cookies.get(0));
        assertTrue(cookies.get(1).startsWith("__atlas_rt=rt1; Path=/"), cookies.get(1));
        assertTrue(cookies.get(1).contains("HttpOnly") && cookies.get(1).contains("Secure"), cookies.get(1));
        assertEquals(10, cookies.size());
        assertEquals(List.of("tok.sig|198.51.100.3|UA/3"), calls);
    }

    @Test
    void aSecondFactorGoesToTheSignInPage() throws Exception {
        results.add(ExchangeResult.needsMfa("sia_1", "needs_second_factor"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter("/sign-in").doFilter(get("/dash", null), response, new MockFilterChain());
        assertEquals(303, response.getStatus());
        assertEquals("/sign-in?__atlas_attempt=sia_1&__atlas_status=needs_second_factor", response.getHeader("Location"));
        assertEquals(8, response.getHeaders("Set-Cookie").size());

        // Without a sign-in page the request continues (unauthenticated), the spent cookies cleared.
        results.add(ExchangeResult.needsMfa("sia_2", "needs_second_factor"));
        MockHttpServletResponse r2 = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter(null).doFilter(get("/dash", null), r2, chain);
        assertNotNull(chain.getRequest());
        assertEquals(8, r2.getHeaders("Set-Cookie").size());
    }

    @Test
    void refusalsContinueAndOnlyFinalOnesClearTheCookie() throws Exception {
        results.add(ExchangeResult.refused("reused", 400));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter(null).doFilter(get("/page", null), response, chain);
        assertNotNull(chain.getRequest());
        assertEquals(200, response.getStatus());
        assertEquals(8, response.getHeaders("Set-Cookie").size());

        results.add(ExchangeResult.refused("expired", 400));
        MockHttpServletResponse guarded = new MockHttpServletResponse();
        filter(null, "/api/**").doFilter(get("/api/me", null), guarded, new MockFilterChain());
        assertEquals(401, guarded.getStatus());
        assertEquals(8, guarded.getHeaders("Set-Cookie").size());

        results.add(ExchangeResult.refused("unavailable", 503));
        MockHttpServletResponse kept = new MockHttpServletResponse();
        filter(null).doFilter(get("/page", null), kept, new MockFilterChain());
        assertTrue(kept.getHeaders("Set-Cookie").isEmpty());
    }

    @Test
    void onlyGetAndHeadAndOnlyWithoutAnAtlasSession() throws Exception {
        MockHttpServletRequest post = get("/form", null);
        post.setMethod("POST");
        filter(null).doFilter(post, new MockHttpServletResponse(), new MockFilterChain());

        MockHttpServletRequest signedIn = get("/page", null);
        String token = jwks.signSession(Map.of());
        signedIn.setCookies(new Cookie("better-auth.session_token", "tok"), new Cookie("__session", token));
        MockFilterChain chain = new MockFilterChain();
        filter(null).doFilter(signedIn, new MockHttpServletResponse(), chain);
        assertNotNull(AtlasContext.get(signedIn));
        assertTrue(calls.isEmpty(), "no exchange");

        results.add(ExchangeResult.session("j", "r", "", "", 0));
        MockHttpServletRequest head = get("/page", null);
        head.setMethod("HEAD");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter(null).doFilter(head, response, new MockFilterChain());
        assertEquals(303, response.getStatus());
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AtlasAutoConfiguration.class))
            .withPropertyValues("atlas.jwks-url=https://api.atlasauth.net/v1/jwks", "atlas.issuer=https://clerk.example.com");

    @Test
    void autoConfigurationWiresTheBridgeAndLegacyIssuers() {
        runner.run(context -> assertThat(context).doesNotHaveBean(SessionBridge.class));
        runner.withPropertyValues("atlas.better-auth-bridge.enabled=true")
                .run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues(
                        "atlas.secret-key=sk_test_123",
                        "atlas.bapi-base-url=http://127.0.0.1:9",
                        "atlas.better-auth-bridge.enabled=true",
                        "atlas.better-auth-bridge.cookie-prefix=myapp",
                        "atlas.better-auth-bridge.sign-in-url=/sign-in",
                        "atlas.legacy-issuers[0].issuer=https://old.example.com",
                        "atlas.legacy-issuers[0].jwks-url=https://old.example.com/api/auth/jwks",
                        "atlas.legacy-issuers[0].until=2099-01-01T00:00:00Z",
                        "atlas.legacy-issuers[0].audience=https://api.old.example.com")
                .run(context -> {
                    assertThat(context).hasSingleBean(SessionBridge.class);
                    assertThat(context).hasSingleBean(SessionVerifier.class);
                    AtlasProperties p = context.getBean(AtlasProperties.class);
                    assertEquals("myapp", p.getBetterAuthBridge().getCookiePrefix());
                    assertEquals("/sign-in", p.getBetterAuthBridge().getSignInUrl());
                    assertEquals("https://old.example.com", p.getLegacyIssuers().get(0).getIssuer());
                    assertEquals(List.of("https://api.old.example.com"), p.getLegacyIssuers().get(0).getAudience());
                    @SuppressWarnings("unchecked")
                    FilterRegistrationBean<AtlasAuthenticationFilter> registration = context.getBean(FilterRegistrationBean.class);
                    assertThat(registration.getFilter()).isInstanceOf(AtlasAuthenticationFilter.class);
                });
        // A legacy issuer without a secret key (default subject lookup) is refused at startup.
        runner.withPropertyValues(
                        "atlas.legacy-issuers[0].issuer=https://old.example.com",
                        "atlas.legacy-issuers[0].jwks-url=https://old.example.com/api/auth/jwks",
                        "atlas.legacy-issuers[0].until=2099-01-01")
                .run(context -> assertThat(context).hasFailed());
    }
}
