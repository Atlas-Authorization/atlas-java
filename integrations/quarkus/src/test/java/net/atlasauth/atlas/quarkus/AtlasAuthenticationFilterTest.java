package net.atlasauth.atlas.quarkus;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AtlasAuthenticationFilterTest {

    static final String ISSUER = "https://auth.example.com";

    static class Resource {
        @AtlasAuthenticated
        public void open() {
        }

        @AtlasAuthenticated
        @AtlasRequires(permission = "org:billing:read")
        public void billing() {
        }

        @AtlasAuthenticated
        @AtlasRequires(scopes = "orders:read")
        public void orders() {
        }
    }

    private RSAKey key;
    private HttpServer server;
    private SessionVerifier verifier;

    @BeforeEach
    void setUp() throws Exception {
        key = new RSAKeyGenerator(2048).keyID("k1").generate();
        byte[] body = new JWKSet(key.toPublicJWK()).toString().getBytes();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/jwks", ex -> {
            ex.sendResponseHeaders(200, body.length);
            ex.getResponseBody().write(body);
            ex.close();
        });
        server.start();
        verifier = SessionVerifier.builder().issuer(ISSUER)
                .jwksUrl(jwksUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/jwks")
                .authorizedParties(List.of("https://app.example.com")).build();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private String token(String issuer, String azp, long ttlMs, Map<String, Object> extra) throws Exception {
        long now = System.currentTimeMillis();
        JWTClaimsSet.Builder c = new JWTClaimsSet.Builder().issuer(issuer).subject("user_1").claim("sid", "s1")
                .claim("azp", azp).issueTime(new Date(now - 1000)).expirationTime(new Date(now + ttlMs));
        extra.forEach(c::claim);
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("k1").build(), c.build());
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    private String good(Map<String, Object> extra) throws Exception {
        return token(ISSUER, "https://app.example.com", 60_000, extra);
    }

    private AtlasAuthenticationFilter filter(String method) throws Exception {
        return filter(method, true);
    }

    private AtlasAuthenticationFilter filter(String method, boolean withAccessTokens) throws Exception {
        ResourceInfo info = mock(ResourceInfo.class);
        Method m = Resource.class.getMethod(method);
        when(info.getResourceMethod()).thenReturn(m);
        when(info.getResourceClass()).thenAnswer(i -> Resource.class);
        AtlasAccessTokens at = withAccessTokens ? AtlasAccessTokens.of(net.atlasauth.atlas.verify.AccessTokenVerifier.builder()
                .issuer(ISSUER).audience(API).jwksUrl(jwksUrl).requiredScopes().build()) : null;
        return new AtlasAuthenticationFilter(verifier, at, info);
    }

    private static final String API = "https://api.example.com";
    private String jwksUrl;

    private String accessToken(String aud, String scope, String tokenUse, long ttlMs) throws Exception {
        long now = System.currentTimeMillis();
        JWTClaimsSet.Builder c = new JWTClaimsSet.Builder().issuer(ISSUER).subject("client_sub").audience(aud).claim("scope", scope)
                .claim("client_id", "cli_1").issueTime(new Date(now - 1000)).expirationTime(new Date(now + ttlMs));
        if (tokenUse != null) {
            c.claim("token_use", tokenUse);
        }
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("k1").type(new com.nimbusds.jose.JOSEObjectType("at+jwt")).build(), c.build());
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    private ContainerRequestContext request(String authorization, String cookie) {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getHeaderString("Authorization")).thenReturn(authorization);
        when(ctx.getCookies()).thenReturn(cookie == null ? Map.of() : Map.of("__session", new Cookie.Builder("__session").value(cookie).build()));
        when(ctx.getSecurityContext()).thenReturn(mock(SecurityContext.class));
        return ctx;
    }

    private static int abortedStatus(ContainerRequestContext ctx) {
        ArgumentCaptor<Response> r = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(r.capture());
        return r.getValue().getStatus();
    }

    @Test
    void validBearerPassesAndExposesClaims() throws Exception {
        ContainerRequestContext ctx = request("Bearer " + good(Map.of()), null);
        filter("open").filter(ctx);
        verify(ctx, never()).abortWith(any());
        ArgumentCaptor<Object> claims = ArgumentCaptor.forClass(Object.class);
        verify(ctx).setProperty(org.mockito.ArgumentMatchers.eq(AtlasContext.PROPERTY), claims.capture());
        assertEquals("user_1", ((net.atlasauth.atlas.verify.SessionClaims) claims.getValue()).sub());
        ArgumentCaptor<SecurityContext> sc = ArgumentCaptor.forClass(SecurityContext.class);
        verify(ctx).setSecurityContext(sc.capture());
        assertEquals("user_1", sc.getValue().getUserPrincipal().getName());
    }

    @Test
    void cookieIsUsedWhenThereIsNoHeader() throws Exception {
        ContainerRequestContext ctx = request(null, good(Map.of()));
        filter("open").filter(ctx);
        verify(ctx, never()).abortWith(any());
    }

    @Test
    void missingExpiredWrongIssuerWrongAzpAndGarbageAre401() throws Exception {
        String[] bad = {
                null,
                "Bearer not.a.jwt",
                "Bearer " + token(ISSUER, "https://app.example.com", -60_000, Map.of()),
                "Bearer " + token("https://evil.example.com", "https://app.example.com", 60_000, Map.of()),
                "Bearer " + token(ISSUER, "https://other.example.com", 60_000, Map.of()),
        };
        for (String authorization : bad) {
            ContainerRequestContext ctx = request(authorization, null);
            filter("open").filter(ctx);
            assertEquals(401, abortedStatus(ctx));
        }
    }

    @Test
    void verifierFailureFailsClosed() throws Exception {
        SessionVerifier down = SessionVerifier.builder().issuer(ISSUER).jwksUrl("http://127.0.0.1:1/jwks").build();
        ResourceInfo info = mock(ResourceInfo.class);
        ContainerRequestContext ctx = request("Bearer " + good(Map.of()), null);
        new AtlasAuthenticationFilter(down, info).filter(ctx);
        assertEquals(401, abortedStatus(ctx));
    }

    @Test
    void requiresAnswers403WithoutThePermissionAndPassesWithIt() throws Exception {
        ContainerRequestContext denied = request("Bearer " + good(Map.of()), null);
        filter("billing").filter(denied);
        assertEquals(403, abortedStatus(denied));

        String granted = good(Map.of("org_id", "o1", "org_role", "admin", "org_permissions", List.of("org:billing:read")));
        ContainerRequestContext ok = request("Bearer " + granted, null);
        filter("billing").filter(ok);
        verify(ok, never()).abortWith(any());
        ArgumentCaptor<SecurityContext> sc = ArgumentCaptor.forClass(SecurityContext.class);
        verify(ok).setSecurityContext(sc.capture());
        assertTrue(sc.getValue().isUserInRole("admin"));
        assertTrue(!sc.getValue().isUserInRole("owner"));
    }

    @Test
    void contextIsNullWithoutASession() {
        assertNull(AtlasContext.claims(null));
    }

    @Test
    void anAccessTokenWithAudienceAndScopePassesAndExposesItsResult() throws Exception {
        ContainerRequestContext ctx = request("Bearer " + accessToken(API, "orders:read orders:write", "access_token", 60_000), null);
        filter("orders").filter(ctx);
        verify(ctx, never()).abortWith(any());
        ArgumentCaptor<Object> at = ArgumentCaptor.forClass(Object.class);
        verify(ctx).setProperty(org.mockito.ArgumentMatchers.eq(AtlasContext.ACCESS_TOKEN_PROPERTY), at.capture());
        net.atlasauth.atlas.verify.AccessTokenVerifier.Result r = (net.atlasauth.atlas.verify.AccessTokenVerifier.Result) at.getValue();
        assertEquals("cli_1", r.clientId());
        assertTrue(r.scopes().contains("orders:write"));
        ArgumentCaptor<SecurityContext> sc = ArgumentCaptor.forClass(SecurityContext.class);
        verify(ctx).setSecurityContext(sc.capture());
        assertTrue(sc.getValue().isUserInRole("orders:read"));
    }

    @Test
    void accessTokensWithAnotherAudienceNoTokenUseOrExpiredAre401() throws Exception {
        String[] bad = {
                accessToken("https://other.example.com", "orders:read", "access_token", 60_000),
                accessToken(API, "orders:read", null, 60_000),
                accessToken(API, "orders:read", "access_token", -60_000),
        };
        for (String t : bad) {
            ContainerRequestContext ctx = request("Bearer " + t, null);
            filter("orders").filter(ctx);
            assertEquals(401, abortedStatus(ctx));
        }
    }

    @Test
    void aMissingScopeIs403AndAnAccessTokenCannotMeetAPermissionRule() throws Exception {
        ContainerRequestContext ctx = request("Bearer " + accessToken(API, "other", "access_token", 60_000), null);
        filter("orders").filter(ctx);
        assertEquals(403, abortedStatus(ctx));

        ContainerRequestContext billing = request("Bearer " + accessToken(API, "orders:read", "access_token", 60_000), null);
        filter("billing").filter(billing);
        assertEquals(403, abortedStatus(billing));
    }

    @Test
    void aSessionCannotMeetAScopeRuleAndAnAccessTokenInTheCookieIsRefused() throws Exception {
        ContainerRequestContext session = request("Bearer " + good(Map.of()), null);
        filter("orders").filter(session);
        assertEquals(403, abortedStatus(session));

        ContainerRequestContext cookie = request(null, accessToken(API, "orders:read", "access_token", 60_000));
        filter("orders").filter(cookie);
        assertEquals(401, abortedStatus(cookie));
    }

    @Test
    void accessTokensAreIgnoredWhenNotConfigured() throws Exception {
        ContainerRequestContext ctx = request("Bearer " + accessToken(API, "orders:read", "access_token", 60_000), null);
        filter("orders", false).filter(ctx);
        assertEquals(401, abortedStatus(ctx));
    }
}
