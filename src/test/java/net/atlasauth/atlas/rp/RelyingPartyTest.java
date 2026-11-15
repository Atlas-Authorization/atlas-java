package net.atlasauth.atlas.rp;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RelyingPartyTest {

    private HttpServer server;
    private String issuer;
    private RSAKey key;
    private volatile String nextNonce;
    private volatile String lastForm;
    private static final String SECRET = "0123456789012345678901234567890123456789";

    @BeforeEach
    void up() throws Exception {
        key = new RSAKeyGenerator(2048).keyID("rp-key").generate();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        issuer = "http://127.0.0.1:" + server.getAddress().getPort();
        server.createContext("/.well-known/openid-configuration", ex -> reply(ex, "{\"issuer\":\"" + issuer + "\",\"authorization_endpoint\":\"" + issuer + "/oauth2/authorize\","
                + "\"token_endpoint\":\"" + issuer + "/oauth2/token\",\"jwks_uri\":\"" + issuer + "/jwks\",\"end_session_endpoint\":\"" + issuer + "/oauth2/logout\"}"));
        server.createContext("/jwks", ex -> reply(ex, new JWKSet(key.toPublicJWK()).toString()));
        server.createContext("/oauth2/token", ex -> {
            lastForm = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                String at = "access-1";
                String atHash = Base64.getUrlEncoder().withoutPadding().encodeToString(java.util.Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(at.getBytes()), 16));
                JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(issuer).audience("client_1").subject("usr_1").claim("nonce", nextNonce).claim("sid", "sess_1")
                        .claim("email", "a@b.c").claim("email_verified", true).claim("auth_time", System.currentTimeMillis() / 1000).claim("at_hash", atHash)
                        .issueTime(new Date()).expirationTime(new Date(System.currentTimeMillis() + 60_000)).build();
                SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("rp-key").build(), claims);
                jwt.sign(new RSASSASigner(key));
                reply(ex, "{\"id_token\":\"" + jwt.serialize() + "\",\"access_token\":\"" + at + "\",\"expires_in\":3600}");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        server.start();
    }

    @AfterEach
    void down() {
        server.stop(0);
    }

    private static void reply(com.sun.net.httpserver.HttpExchange ex, String body) throws java.io.IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("content-type", "application/json");
        ex.sendResponseHeaders(200, b.length);
        ex.getResponseBody().write(b);
        ex.close();
    }

    private RelyingParty rp() {
        return new RelyingParty(RpConfig.builder(issuer, "client_1", "https://app.example.com/auth/callback", SECRET).clientSecret("shh").build());
    }

    private static Map<String, String> query(String url) {
        Map<String, String> q = new LinkedHashMap<>();
        for (String p : URI.create(url).getRawQuery().split("&")) {
            String[] kv = p.split("=", 2);
            q.put(kv[0], URLDecoder.decode(kv.length > 1 ? kv[1] : "", StandardCharsets.UTF_8));
        }
        return q;
    }

    @Test
    void startThenCallbackVerifiesStatePkceAndTheIdToken() {
        RelyingParty rp = rp();
        RelyingParty.Redirect go = rp.start(Map.of("return_to", "/dash", "ref", "x"));
        Map<String, String> q = query(go.location());
        assertEquals("S256", q.get("code_challenge_method"));
        assertEquals("client_1", q.get("client_id"));
        nextNonce = q.get("nonce");
        String cookie = go.cookies().get(0).split(";")[0];
        assertTrue(cookie.startsWith("atlas_oidc=s1."));
        RelyingParty.CallbackResult done = rp.callback(Map.of("state", q.get("state"), "code", "c1"), cookie);
        assertEquals("usr_1", done.user().sub());
        assertEquals("sess_1", done.user().sid());
        assertTrue(done.user().emailVerified());
        assertEquals("/dash", done.returnTo());
        assertTrue(lastForm.contains("code_verifier="));
        assertTrue(done.clearCookie().contains("Max-Age=0"));
    }

    @Test
    void aWrongStateAMissingCookieAndAWrongNonceAreRefused() {
        RelyingParty rp = rp();
        RelyingParty.Redirect go = rp.start(Map.of());
        Map<String, String> q = query(go.location());
        String cookie = go.cookies().get(0).split(";")[0];
        nextNonce = q.get("nonce");
        assertEquals("state_mismatch", assertThrows(OidcException.class, () -> rp.callback(Map.of("state", "x", "code", "c"), cookie)).code());
        assertEquals("state_mismatch", assertThrows(OidcException.class, () -> rp.callback(Map.of("state", q.get("state"), "code", "c"), null)).code());
        nextNonce = "other";
        OidcException e = assertThrows(OidcException.class, () -> rp.callback(Map.of("state", q.get("state"), "code", "c"), cookie));
        assertEquals("invalid_token", e.code());
        assertNotNull(e.clearCookie());
    }

    @Test
    void requireRecentAuthSkipsWhenFreshAndAsksTheSameUserOtherwise() {
        RelyingParty rp = rp();
        assertNull(rp.requireRecentAuth("usr_1", System.currentTimeMillis() / 1000, 300, "/x"));
        RelyingParty.Redirect go = rp.requireRecentAuth("usr_1", 1L, 300, "/x");
        assertEquals("300", query(go.location()).get("max_age"));
        assertTrue(RelyingParty.isSafeReturnTo("/a?b=1"));
        assertFalse(RelyingParty.isSafeReturnTo("//evil.example"));
    }
}
