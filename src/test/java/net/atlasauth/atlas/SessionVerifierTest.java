package net.atlasauth.atlas;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import net.atlasauth.atlas.verify.ForbiddenError;
import net.atlasauth.atlas.verify.ProtectCondition;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Local session-token verification: a real RSA key signs real JWTs, a loopback
 * server serves the public JWKS, and the verifier runs offline against it. A
 * fixed clock keeps the exp/nbf assertions deterministic.
 */
class SessionVerifierTest {

    private static final String ISSUER = "https://clerk.example.com";
    private static final String KID = "test-key-1";
    /** Fixed "now": 2023-11-14T22:13:20Z (epoch millis). */
    private static final long NOW_MILLIS = 1_700_000_000_000L;

    private HttpServer jwksServer;
    private RSAKey rsaKey;
    private String jwksUrl;

    @BeforeEach
    void setUp() throws Exception {
        rsaKey = new RSAKeyGenerator(2048).keyID(KID).generate();
        String jwks = new com.nimbusds.jose.jwk.JWKSet(rsaKey.toPublicJWK()).toString();

        jwksServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        jwksServer.createContext("/v1/jwks", exchange -> {
            byte[] body = jwks.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("content-type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
            exchange.close();
        });
        jwksServer.start();
        jwksUrl = "http://127.0.0.1:" + jwksServer.getAddress().getPort() + "/v1/jwks";
    }

    @AfterEach
    void tearDown() {
        jwksServer.stop(0);
    }

    private SessionVerifier verifier(String issuer) {
        return SessionVerifier.builder()
                .jwksUrl(jwksUrl)
                .issuer(issuer)
                .now(() -> NOW_MILLIS)
                .build();
    }

    private String signSession(String issuer, long expMillis, Map<String, Object> extra) throws Exception {
        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject("usr_1")
                .claim("sid", "sess_1")
                .issueTime(new Date(NOW_MILLIS - 1000))
                .notBeforeTime(new Date(NOW_MILLIS - 1000))
                .expirationTime(new Date(expMillis));
        extra.forEach(claims::claim);

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KID).build(),
                claims.build());
        JWSSigner signer = new RSASSASigner(rsaKey);
        jwt.sign(signer);
        return jwt.serialize();
    }

    @Test
    void verifiesAValidSessionAndExposesClaims() throws Exception {
        String token = signSession(ISSUER, NOW_MILLIS + 60_000, Map.of(
                "org_id", "org_1",
                "org_role", "admin",
                "org_permissions", List.of("org:reports:read")));

        VerifyResult result = verifier(ISSUER).verify(token);

        assertTrue(result.ok());
        assertEquals("usr_1", result.claims().sub());
        assertEquals("sess_1", result.claims().sid());
        assertEquals("org_1", result.claims().orgId());
        assertEquals("admin", result.claims().orgRole());
    }

    @Test
    void protectPassesWhenPermissionHeld() throws Exception {
        String token = signSession(ISSUER, NOW_MILLIS + 60_000, Map.of(
                "org_role", "admin",
                "org_permissions", List.of("org:reports:read")));

        VerifyResult result = verifier(ISSUER).verify(token);

        assertTrue(result.has(new ProtectCondition().permission("org:reports:read")));
        assertTrue(result.has(new ProtectCondition().role("admin")));
        // protect() returns the claims when satisfied.
        assertEquals("usr_1", result.protect(new ProtectCondition().role("admin")).sub());
    }

    @Test
    void protectThrowsForbiddenWhenPermissionMissing() throws Exception {
        String token = signSession(ISSUER, NOW_MILLIS + 60_000, Map.of(
                "org_role", "member",
                "org_permissions", List.of("org:reports:read")));

        VerifyResult result = verifier(ISSUER).verify(token);

        assertFalse(result.has(new ProtectCondition().permission("org:billing:manage")));
        assertThrows(ForbiddenError.class,
                () -> result.protect(new ProtectCondition().permission("org:billing:manage")));
    }

    @Test
    void rejectsWrongIssuer() throws Exception {
        String token = signSession("https://evil.example.com", NOW_MILLIS + 60_000, Map.of());

        VerifyResult result = verifier(ISSUER).verify(token);

        assertFalse(result.ok());
        assertEquals(VerifyResult.Reason.INVALID, result.reason());
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        // Expired a full minute ago — beyond the 5s skew.
        String token = signSession(ISSUER, NOW_MILLIS - 60_000, Map.of());

        VerifyResult result = verifier(ISSUER).verify(token);

        assertFalse(result.ok());
        assertEquals(VerifyResult.Reason.INVALID, result.reason());
    }

    @Test
    void rejectsTamperedSignature() throws Exception {
        String token = signSession(ISSUER, NOW_MILLIS + 60_000, Map.of());
        // Flip the last character of the signature segment.
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1) + (last == 'A' ? 'B' : 'A');

        VerifyResult result = verifier(ISSUER).verify(tampered);

        assertFalse(result.ok());
    }

    @Test
    void rejectsMalformedToken() {
        VerifyResult result = verifier(ISSUER).verify("not-a-jwt");

        assertFalse(result.ok());
        assertEquals(VerifyResult.Reason.MALFORMED, result.reason());
    }

    @Test
    void rejectsOpAccessTokenReplayedAsSession() throws Exception {
        // token_use=access (or a present aud) must not pass as a session (§13.1).
        String token = signSession(ISSUER, NOW_MILLIS + 60_000, Map.of("token_use", "access"));

        VerifyResult result = verifier(ISSUER).verify(token);

        assertFalse(result.ok());
        assertEquals(VerifyResult.Reason.INVALID, result.reason());
    }

    @Test
    void authenticateRequestPrefersBearerHeader() throws Exception {
        String token = signSession(ISSUER, NOW_MILLIS + 60_000, Map.of());

        VerifyResult result = verifier(ISSUER)
                .authenticateRequest(Map.of("authorization", "Bearer " + token));

        assertTrue(result.ok());
    }

    @Test
    void authenticateRequestReadsSessionCookie() throws Exception {
        String token = signSession(ISSUER, NOW_MILLIS + 60_000, Map.of());

        VerifyResult result = verifier(ISSUER)
                .authenticateRequest(Map.of("cookie", "foo=bar; __session=" + token + "; baz=qux"));

        assertTrue(result.ok());
    }
}
