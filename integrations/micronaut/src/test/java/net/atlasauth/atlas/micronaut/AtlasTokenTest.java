package net.atlasauth.atlas.micronaut;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.authentication.Authentication;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtlasTokenTest {

    static final String ISSUER = "https://auth.example.com";
    static final String APP = "https://app.example.com";
    static final String API = "https://api.example.com";

    private RSAKey key;
    private HttpServer server;
    private AtlasTokenValidator validator;
    private String jwksUrl;

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
        jwksUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/jwks";
        validator = new AtlasTokenValidator(SessionVerifier.builder().issuer(ISSUER).jwksUrl(jwksUrl)
                .authorizedParties(List.of(APP)).build(),
                net.atlasauth.atlas.verify.AccessTokenVerifier.builder().issuer(ISSUER).jwksUrl(jwksUrl).audience(API).build());
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

    private Authentication validate(String token) {
        return Mono.from(validator.validateToken(token, HttpRequest.GET("/").header("Authorization", "Bearer " + token))).block();
    }

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

    @Test
    void validTokenBecomesAnAuthenticationWithRoles() throws Exception {
        Authentication a = validate(token(ISSUER, APP, 60_000,
                Map.of("org_id", "o1", "org_role", "admin", "org_permissions", List.of("org:billing:read"), "app_roles", List.of("staff"))));
        assertNotNull(a);
        assertEquals("user_1", a.getName());
        assertTrue(a.getRoles().containsAll(List.of("admin", "staff", "org:billing:read")));
        assertEquals("o1", a.getAttributes().get("org_id"));
    }

    @Test
    void expiredWrongIssuerWrongAzpAndGarbageGiveNoAuthentication() throws Exception {
        assertNull(validate(token(ISSUER, APP, -60_000, Map.of())));
        assertNull(validate(token("https://evil.example.com", APP, 60_000, Map.of())));
        assertNull(validate(token(ISSUER, "https://other.example.com", 60_000, Map.of())));
        assertNull(validate("not.a.jwt"));
    }

    @Test
    void unreachableKeySetFailsClosed() throws Exception {
        AtlasTokenValidator down = new AtlasTokenValidator(
                SessionVerifier.builder().issuer(ISSUER).jwksUrl("http://127.0.0.1:1/jwks").build());
        assertNull(Mono.from(down.validateToken(token(ISSUER, APP, 60_000, Map.of()), HttpRequest.GET("/"))).block());
    }

    @Test
    void readerPrefersBearerThenFallsBackToTheCookie() {
        AtlasConfiguration config = new AtlasConfiguration();
        AtlasTokenReader reader = new AtlasTokenReader(config);
        assertEquals(Optional.of("abc"), reader.findToken(HttpRequest.GET("/").header("Authorization", "bearer abc")));
        assertEquals(Optional.of("xyz"), reader.findToken(HttpRequest.GET("/").cookie(Cookie.of("__session", "xyz"))));
        assertEquals(Optional.of("abc"), reader.findToken(
                HttpRequest.GET("/").header("Authorization", "Bearer abc").cookie(Cookie.of("__session", "xyz"))));
        assertEquals(Optional.empty(), reader.findToken(HttpRequest.GET("/")));
    }

    @Test
    void anAccessTokenBecomesAnAuthenticationWithScopesAsRoles() throws Exception {
        Authentication a = validate(accessToken(API, "orders:read orders:write", "access_token", 60_000));
        assertNotNull(a);
        assertEquals("client_sub", a.getName());
        assertTrue(a.getRoles().containsAll(List.of("orders:read", "orders:write")));
        assertEquals("cli_1", a.getAttributes().get("client_id"));
    }

    @Test
    void accessTokensWithAnotherAudienceNoTokenUseOrExpiredGiveNoAuthentication() throws Exception {
        assertNull(validate(accessToken("https://other.example.com", "orders:read", "access_token", 60_000)));
        assertNull(validate(accessToken(API, "orders:read", null, 60_000)));
        assertNull(validate(accessToken(API, "orders:read", "access_token", -60_000)));
    }

    @Test
    void aRequiredScopeIsEnforcedAndCookieDeliveredAccessTokensAreRefused() throws Exception {
        AtlasTokenValidator scoped = new AtlasTokenValidator(SessionVerifier.builder().issuer(ISSUER).jwksUrl(jwksUrl).build(),
                net.atlasauth.atlas.verify.AccessTokenVerifier.builder().issuer(ISSUER).jwksUrl(jwksUrl).audience(API).requiredScopes("orders:read").build());
        String noScope = accessToken(API, "other", "access_token", 60_000);
        assertNull(Mono.from(scoped.validateToken(noScope, HttpRequest.GET("/").header("Authorization", "Bearer " + noScope))).block());

        String good = accessToken(API, "orders:read", "access_token", 60_000);
        // No Authorization header (the token came from the session cookie): not accepted as an access token.
        assertNull(Mono.from(validator.validateToken(good, HttpRequest.GET("/"))).block());
    }

    @Test
    void accessTokensAreIgnoredWhenNoAccessVerifierIsConfigured() throws Exception {
        AtlasTokenValidator sessionsOnly = new AtlasTokenValidator(SessionVerifier.builder().issuer(ISSUER).jwksUrl(jwksUrl).build());
        String t = accessToken(API, "orders:read", "access_token", 60_000);
        assertNull(Mono.from(sessionsOnly.validateToken(t, HttpRequest.GET("/").header("Authorization", "Bearer " + t))).block());
    }
}
