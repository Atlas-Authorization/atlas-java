package net.atlasauth.atlas;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import net.atlasauth.atlas.verify.LogoutTokenClaims;
import net.atlasauth.atlas.verify.LogoutTokenException;
import net.atlasauth.atlas.verify.LogoutTokenException.Reason;
import net.atlasauth.atlas.verify.LogoutTokenVerifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** OIDC Back-Channel Logout 1.0 §2.6 checks, against a real RSA key served from a loopback JWKS. */
class LogoutTokenVerifierTest {

    private static final String ISSUER = "https://atlas.example.com";
    private static final String CLIENT = "atlas_client_1";
    private static final String KID = "logout-test-key";
    private static final long NOW_MS = 1_800_000_000_000L;

    private static HttpServer server;
    private static RSAKey key;
    private static RSAKey otherKey;
    private static LogoutTokenVerifier verifier;

    @BeforeAll
    static void start() throws Exception {
        key = new RSAKeyGenerator(2048).keyID(KID).generate();
        otherKey = new RSAKeyGenerator(2048).keyID(KID).generate();
        String jwks = new JWKSet(key.toPublicJWK()).toString();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/jwks", ex -> {
            byte[] b = jwks.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("content-type", "application/json");
            ex.sendResponseHeaders(200, b.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(b);
            }
            ex.close();
        });
        server.start();
        verifier = LogoutTokenVerifier.builder()
                .jwksUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/v1/jwks")
                .issuer(ISSUER)
                .clientId(CLIENT)
                .now(() -> NOW_MS)
                .build();
    }

    @AfterAll
    static void stop() {
        server.stop(0);
    }

    private static JWTClaimsSet.Builder validClaims() {
        return new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject("usr_1")
                .audience(CLIENT)
                .issueTime(new Date(NOW_MS - 1_000))
                .expirationTime(new Date(NOW_MS + 120_000))
                .jwtID("jti_1")
                .claim("sid", "sess_1")
                .claim("events", Map.of(LogoutTokenVerifier.BACKCHANNEL_LOGOUT_EVENT, Map.of()));
    }

    private static String sign(JWTClaimsSet claims, String typ, RSAKey signer) throws Exception {
        JWSHeader.Builder h = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KID);
        if (typ != null) {
            h.type(new JOSEObjectType(typ));
        }
        SignedJWT jwt = new SignedJWT(h.build(), claims);
        jwt.sign(new RSASSASigner(signer));
        return jwt.serialize();
    }

    private static String token(Consumer<JWTClaimsSet.Builder> edit) throws Exception {
        JWTClaimsSet.Builder b = validClaims();
        edit.accept(b);
        return sign(b.build(), "logout+jwt", key);
    }

    private static void assertReason(Reason reason, String token) {
        LogoutTokenException e = assertThrows(LogoutTokenException.class, () -> verifier.verify(token));
        assertEquals(reason, e.reason());
    }

    @Test
    void validTokenReturnsItsClaims() throws Exception {
        LogoutTokenClaims c = verifier.verify(token(b -> { }));
        assertEquals("usr_1", c.sub());
        assertEquals("sess_1", c.sid());
        assertEquals("jti_1", c.jti());
        assertEquals((NOW_MS - 1_000) / 1000, c.iat());
    }

    @Test
    void formBodyIsParsed() throws Exception {
        String body = "foo=bar&logout_token=" + URLEncoder.encode(token(b -> { }), StandardCharsets.UTF_8);
        assertEquals("sess_1", verifier.verifyFormBody(body).sid());
        assertReason(Reason.MALFORMED, LogoutTokenVerifier.extractLogoutToken("foo=bar"));
    }

    @Test
    void sidAloneOrSubAloneIsEnough() throws Exception {
        assertNull(verifier.verify(token(b -> b.subject(null))).sub());
        assertNull(verifier.verify(token(b -> b.claim("sid", null))).sid());
        assertReason(Reason.MISSING_SUBJECT, token(b -> b.subject(null).claim("sid", null)));
    }

    @Test
    void wrongTypIsRefused() throws Exception {
        assertReason(Reason.WRONG_TYPE, sign(validClaims().build(), "JWT", key));
        // typ is optional
        assertEquals("jti_1", verifier.verify(sign(validClaims().build(), null, key)).jti());
    }

    @Test
    void nonceIsRefused() throws Exception {
        assertReason(Reason.HAS_NONCE, token(b -> b.claim("nonce", "n1")));
    }

    @Test
    void missingOrWrongEventsAreRefused() throws Exception {
        assertReason(Reason.MISSING_EVENT, token(b -> b.claim("events", null)));
        assertReason(Reason.MISSING_EVENT, token(b -> b.claim("events", Map.of("other", Map.of()))));
        assertReason(Reason.MISSING_EVENT,
                token(b -> b.claim("events", Map.of(LogoutTokenVerifier.BACKCHANNEL_LOGOUT_EVENT, "yes"))));
    }

    @Test
    void wrongAudienceIsRefused() throws Exception {
        assertReason(Reason.WRONG_AUDIENCE, token(b -> b.audience("someone_else")));
    }

    @Test
    void wrongIssuerIsRefused() throws Exception {
        assertReason(Reason.WRONG_ISSUER, token(b -> b.issuer("https://evil.example.com")));
    }

    @Test
    void expiredIsRefused() throws Exception {
        assertReason(Reason.EXPIRED, token(b -> b.expirationTime(new Date(NOW_MS - 120_000))));
    }

    @Test
    void iatRules() throws Exception {
        assertReason(Reason.MISSING_IAT, token(b -> b.issueTime(null)));
        assertReason(Reason.ISSUED_IN_FUTURE, token(b -> b.issueTime(new Date(NOW_MS + 600_000))));
    }

    @Test
    void missingJtiIsRefused() throws Exception {
        assertReason(Reason.MISSING_JTI, token(b -> b.jwtID(null)));
    }

    @Test
    void badSignatureIsRefused() throws Exception {
        assertReason(Reason.BAD_SIGNATURE, sign(validClaims().build(), "logout+jwt", otherKey));
    }

    @Test
    void garbageIsMalformed() {
        assertReason(Reason.MALFORMED, "not-a-jwt");
        assertReason(Reason.MALFORMED, null);
    }

    @Test
    void unreachableJwksIsNoKeysAndRetryable() throws Exception {
        LogoutTokenVerifier unreachable = LogoutTokenVerifier.builder()
                .jwksUrl("http://127.0.0.1:1/v1/jwks")
                .issuer(ISSUER)
                .clientId(CLIENT)
                .now(() -> NOW_MS)
                .build();
        String t = token(b -> { });
        LogoutTokenException e = assertThrows(LogoutTokenException.class, () -> unreachable.verify(t));
        assertEquals(Reason.NO_KEYS, e.reason());
        assertTrue(e.retryable());
    }

    @Test
    void aBadTokenIsNotRetryable() throws Exception {
        String t = token(b -> b.audience("someone-else"));
        LogoutTokenException e = assertThrows(LogoutTokenException.class, () -> verifier.verify(t));
        assertFalse(e.retryable());
    }
}
