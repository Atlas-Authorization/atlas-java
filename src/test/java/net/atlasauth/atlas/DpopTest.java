package net.atlasauth.atlas;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import net.atlasauth.atlas.spring.AtlasAccessTokenFilter;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import net.atlasauth.atlas.verify.Dpop;
import net.atlasauth.atlas.verify.HmacDpopNonceSource;
import net.atlasauth.atlas.verify.MemoryDpopReplayStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DPoP (RFC 9449) on the access-token verifier: the same vectors as the TypeScript
 * {@code @atlasauth/backend} suite, plus every other refusal path. Keys are generated per run; the
 * proofs are genuine ES256 / RS256 / PS256 / EdDSA JWSs.
 */
class DpopTest {

    private static final String ISSUER = "https://auth.example.com";
    private static final String API = "https://api.example.com/mcp";
    private static final String META = "https://api.example.com/.well-known/oauth-protected-resource/mcp";

    private HttpServer jwksServer;
    private String jwksUrl;
    private ECKey as;

    /** A client key: its public JWK and a signer over the JWS signing input. */
    record Key(String alg, Map<String, Object> jwk, String jkt, Function<byte[], byte[]> sign) {
    }

    @BeforeEach
    void setUp() throws Exception {
        as = new ECKeyGenerator(Curve.P_256).keyID("k1").algorithm(JWSAlgorithm.ES256).generate();
        String jwks = new JWKSet(List.of(as.toPublicJWK())).toString();
        jwksServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        jwksServer.createContext("/jwks", exchange -> {
            byte[] body = jwks.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        jwksServer.start();
        jwksUrl = "http://127.0.0.1:" + jwksServer.getAddress().getPort() + "/jwks";
    }

    @AfterEach
    void tearDown() {
        jwksServer.stop(0);
    }

    static Key key(String alg) throws Exception {
        switch (alg) {
            case "ES256" -> {
                ECKey k = new ECKeyGenerator(Curve.P_256).generate();
                ECDSASigner signer = new ECDSASigner(k);
                Map<String, Object> jwk = k.toPublicJWK().toJSONObject();
                return new Key(alg, jwk, Dpop.jwkThumbprint(jwk), in -> nimbusSign(signer::sign, JWSAlgorithm.ES256, in));
            }
            case "RS256", "PS256" -> {
                RSAKey k = new RSAKeyGenerator(2048).generate();
                RSASSASigner signer = new RSASSASigner(k);
                Map<String, Object> jwk = k.toPublicJWK().toJSONObject();
                return new Key(alg, jwk, Dpop.jwkThumbprint(jwk), in -> nimbusSign(signer::sign, JWSAlgorithm.parse(alg), in));
            }
            default -> {
                KeyPair kp = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
                byte[] spki = kp.getPublic().getEncoded();
                Map<String, Object> jwk = new LinkedHashMap<>();
                jwk.put("kty", "OKP");
                jwk.put("crv", "Ed25519");
                jwk.put("x", Base64URL.encode(Arrays.copyOfRange(spki, spki.length - 32, spki.length)).toString());
                return new Key(alg, jwk, Dpop.jwkThumbprint(jwk), in -> {
                    try {
                        Signature s = Signature.getInstance("Ed25519");
                        s.initSign(kp.getPrivate());
                        s.update(in);
                        return s.sign();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
        }
    }

    interface NimbusSign {
        Base64URL sign(JWSHeader h, byte[] in) throws Exception;
    }

    static byte[] nimbusSign(NimbusSign s, JWSAlgorithm alg, byte[] in) {
        try {
            return s.sign(new JWSHeader.Builder(alg).build(), in).decode();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static String b64(String json) {
        return Base64URL.encode(json.getBytes(StandardCharsets.UTF_8)).toString();
    }

    /** A proof; {@code claims} values of null delete the default, {@code header} replaces the default header. */
    static String proof(Key k, String token, Map<String, Object> claims, Map<String, Object> header, boolean tamper) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("htm", "POST");
        body.put("htu", API);
        body.put("jti", UUID.randomUUID().toString());
        body.put("iat", System.currentTimeMillis() / 1000);
        body.put("ath", Dpop.accessTokenHash(token));
        claims.forEach((c, v) -> {
            if (v == null) {
                body.remove(c);
            } else {
                body.put(c, v);
            }
        });
        Map<String, Object> h = header;
        if (h == null) {
            h = new LinkedHashMap<>();
            h.put("typ", "dpop+jwt");
            h.put("alg", k.alg());
            h.put("jwk", k.jwk());
        }
        String input = b64(JSONObjectUtils.toJSONString(h)) + "." + b64(JSONObjectUtils.toJSONString(body));
        byte[] sig = k.sign().apply(input.getBytes(StandardCharsets.US_ASCII));
        if (tamper) {
            input = b64(JSONObjectUtils.toJSONString(h)) + "." + b64("{\"htm\":\"POST\",\"jti\":\"x\"}");
        }
        return input + "." + Base64URL.encode(sig);
    }

    static String proof(Key k, String token) {
        return proof(k, token, Map.of(), null, false);
    }

    static String proof(Key k, String token, Map<String, Object> claims) {
        return proof(k, token, claims, null, false);
    }

    static Map<String, Object> claims(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    String token(String jkt) throws Exception {
        long now = System.currentTimeMillis();
        JWTClaimsSet.Builder c = new JWTClaimsSet.Builder().issuer(ISSUER).subject("user_1").audience(API)
                .expirationTime(new Date(now + 300_000)).claim("token_use", "access_token").claim("scope", "tools:read")
                .claim("client_id", "cli_1").jwtID(UUID.randomUUID().toString());
        if (jkt != null) {
            c.claim("cnf", Map.of("jkt", jkt));
        }
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256).keyID("k1").build(), c.build());
        jwt.sign(new ECDSASigner(as));
        return jwt.serialize();
    }

    AccessTokenVerifier verifier(Consumer<AccessTokenVerifier.Builder> mod) {
        AccessTokenVerifier.Builder b = AccessTokenVerifier.builder().issuer(ISSUER).audience(API).jwksUrl(jwksUrl)
                .resourceMetadataUrl(META);
        if (mod != null) {
            mod.accept(b);
        }
        return b.build();
    }

    static AccessTokenVerifier.Result check(AccessTokenVerifier v, String scheme, String tok, String proof) {
        return check(v, scheme, tok, proof, "POST", API + "?x=1");
    }

    static AccessTokenVerifier.Result check(AccessTokenVerifier v, String scheme, String tok, String proof, String method, String url) {
        return v.verifyRequest(new AccessTokenVerifier.ResourceRequest(method, url, scheme + " " + tok, proof));
    }

    static void assertRefused(AccessTokenVerifier.Result r, String error, String name) {
        assertFalse(r.ok(), name);
        assertEquals(401, r.status(), name);
        assertEquals(error, r.error(), name + ": " + r.description());
        assertTrue(r.wwwAuthenticate().startsWith("DPoP "), name);
        assertTrue(r.wwwAuthenticate().contains("error=\"" + error + "\""), name);
        assertTrue(r.wwwAuthenticate().contains("algs=\"ES256"), name);
    }

    @Test
    void acceptsBoundTokenWithMatchingProofForEveryAlg() throws Exception {
        AccessTokenVerifier v = verifier(null);
        for (String alg : List.of("ES256", "RS256", "PS256", "EdDSA")) {
            Key k = key(alg);
            String tok = token(k.jkt());
            AccessTokenVerifier.Result r = check(v, "DPoP", tok, proof(k, tok));
            assertTrue(r.ok(), alg + ": " + r.description());
            assertTrue(r.dpopBound());
            assertEquals("user_1", r.subject());
        }
        AccessTokenVerifier.Result plain = check(v, "Bearer", token(null), null);
        assertTrue(plain.ok());
        assertFalse(plain.dpopBound());
    }

    @Test
    void neverAcceptsBoundTokenAsBearer() throws Exception {
        Key k = key("ES256");
        String tok = token(k.jkt());
        assertRefused(check(verifier(null), "Bearer", tok, proof(k, tok)), "invalid_token", "bearer");
        assertRefused(verifier(null).verify("DPoP " + tok), "invalid_dpop_proof", "no proof");
        String unbound = token(null);
        assertRefused(check(verifier(null), "DPoP", unbound, proof(k, unbound)), "invalid_token", "unbound as DPoP");
    }

    @Test
    void rejectsEveryBadProof() throws Exception {
        AccessTokenVerifier v = verifier(b -> b.dpopReplayStore(new MemoryDpopReplayStore()));
        Key k = key("ES256");
        Key other = key("ES256");
        String tok = token(k.jkt());
        long now = System.currentTimeMillis() / 1000;
        Map<String, Object> privJwk = new LinkedHashMap<>(k.jwk());
        privJwk.put("d", "AAAA");
        Map<String, String> cases = new LinkedHashMap<>();
        cases.put("missing proof", null);
        cases.put("malformed", "not-a-jwt");
        cases.put("garbage header", "e30.e30.e30");
        cases.put("another key", proof(other, tok));
        cases.put("wrong htm", proof(k, tok, claims("htm", "GET")));
        cases.put("wrong htu", proof(k, tok, claims("htu", "https://api.example.com/other")));
        cases.put("wrong host", proof(k, tok, claims("htu", "https://evil.example.com/mcp")));
        cases.put("relative htu", proof(k, tok, claims("htu", "/mcp")));
        cases.put("wrong ath", proof(k, tok, claims("ath", "nope")));
        cases.put("no ath", proof(k, tok, claims("ath", null)));
        cases.put("proof for another token", proof(k, token(k.jkt())));
        cases.put("stale", proof(k, tok, claims("iat", now - 3600)));
        cases.put("future", proof(k, tok, claims("iat", now + 60)));
        cases.put("no iat", proof(k, tok, claims("iat", null)));
        cases.put("expired exp", proof(k, tok, claims("exp", now - 60)));
        cases.put("no jti", proof(k, tok, claims("jti", null)));
        cases.put("typ JWT", proof(k, tok, Map.of(), claims("typ", "JWT", "alg", "ES256", "jwk", k.jwk()), false));
        cases.put("alg HS256", proof(k, tok, Map.of(), claims("typ", "dpop+jwt", "alg", "HS256", "jwk", k.jwk()), false));
        cases.put("alg none", proof(k, tok, Map.of(), claims("typ", "dpop+jwt", "alg", "none", "jwk", k.jwk()), false));
        cases.put("no jwk", proof(k, tok, Map.of(), claims("typ", "dpop+jwt", "alg", "ES256"), false));
        cases.put("private jwk", proof(k, tok, Map.of(), claims("typ", "dpop+jwt", "alg", "ES256", "jwk", privJwk), false));
        cases.put("alg/key mismatch", proof(key("RS256"), tok, Map.of(), claims("typ", "dpop+jwt", "alg", "RS256", "jwk", k.jwk()), false));
        cases.put("bad signature", proof(k, tok, Map.of(), null, true));
        cases.forEach((name, p) -> assertRefused(check(v, "DPoP", tok, p), "invalid_dpop_proof", name));

        String once = proof(k, tok, claims("jti", "fixed"));
        assertTrue(check(v, "DPoP", tok, once).ok());
        assertRefused(check(v, "DPoP", tok, once), "invalid_dpop_proof", "replay");
        String tokOther = token(other.jkt());
        assertTrue(check(v, "DPoP", tokOther, proof(other, tokOther, claims("jti", "fixed"))).ok(), "same jti, other key");
    }

    @Test
    void requireDpopRefusesUnboundToken() throws Exception {
        AccessTokenVerifier v = verifier(b -> b.requireDpop(true));
        assertRefused(check(v, "Bearer", token(null), null), "invalid_token", "requireDpop");
        AccessTokenVerifier.Result none = v.verify(null);
        assertEquals(401, none.status());
        assertNull(none.error());
        assertTrue(none.wwwAuthenticate().matches("^DPoP .*algs=\".*"), none.wwwAuthenticate());
    }

    @Test
    void htuNormalisationAndPublicOrigin() throws Exception {
        Key k = key("ES256");
        String tok = token(k.jkt());
        AccessTokenVerifier v = verifier(null);
        for (String htu : List.of("HTTPS://API.Example.COM:443/mcp", "https://api.example.com/mcp?other=2#frag")) {
            assertTrue(check(v, "DPoP", tok, proof(k, tok, claims("htu", htu))).ok(), htu);
        }
        assertRefused(check(v, "DPoP", tok, proof(k, tok), "POST", "/mcp?x=1"), "invalid_dpop_proof", "path, no origin");
        AccessTokenVerifier vo = verifier(b -> b.publicOrigin("https://api.example.com"));
        assertTrue(check(vo, "DPoP", tok, proof(k, tok), "post", "/mcp?x=1").ok());
        Map<String, String> norm = Map.of(
                "https://A.example.com:443", "https://a.example.com/",
                "http://a.example.com:80/x?y#z", "http://a.example.com/x",
                "https://a.example.com:8443/p/", "https://a.example.com:8443/p/",
                "https://[::1]:443/p", "https://[::1]/p");
        norm.forEach((given, want) -> assertEquals(want, Dpop.normalizeHtu(given), given));
        assertNull(Dpop.normalizeHtu("ftp://x/y"));
    }

    @Test
    void serverNonce() throws Exception {
        long[] clock = {System.currentTimeMillis()};
        HmacDpopNonceSource nonces = new HmacDpopNonceSource("s".repeat(32).getBytes(StandardCharsets.UTF_8), 60, () -> clock[0]);
        AccessTokenVerifier v = verifier(b -> b.dpopNonces(nonces));
        Key k = key("ES256");
        String tok = token(k.jkt());
        AccessTokenVerifier.Result r = check(v, "DPoP", tok, proof(k, tok));
        assertRefused(r, "use_dpop_nonce", "no nonce");
        assertEquals(nonces.current(), r.dpopNonce());
        assertRefused(check(v, "DPoP", tok, proof(k, tok, claims("nonce", "made-up"))), "use_dpop_nonce", "wrong nonce");
        AccessTokenVerifier.Result ok = check(v, "DPoP", tok, proof(k, tok, claims("nonce", r.dpopNonce())));
        assertTrue(ok.ok(), ok.description());
        assertNotNull(ok.dpopNonce());
        String issued = nonces.current();
        clock[0] += 60_000;
        assertTrue(nonces.valid(issued));
        clock[0] += 60_000;
        assertFalse(nonces.valid(issued));
        assertRefused(check(v, "DPoP", tok, proof(k, tok, claims("nonce", issued))), "use_dpop_nonce", "stale nonce");
        assertThrows(IllegalArgumentException.class, () -> new HmacDpopNonceSource("short".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void helpers() {
        String n = "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqbw0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw";
        assertEquals("NzbLsXh8uDCcd-6MNwXF4W_7noWXFZAfHkxZsRGC9Xs",
                Dpop.jwkThumbprint(claims("kty", "RSA", "e", "AQAB", "alg", "RS256", "kid", "2011-04-29", "n", n)));
        assertThrows(IllegalArgumentException.class, () -> Dpop.jwkThumbprint(claims("kty", "oct", "k", "x")));
        long[] clock = {1_000_000};
        MemoryDpopReplayStore store = new MemoryDpopReplayStore(2, () -> clock[0]);
        assertTrue(store.claim("a", 1_060_000));
        assertFalse(store.claim("a", 1_060_000));
        clock[0] = 1_120_000;
        assertTrue(store.claim("a", 1_180_000));
        store.claim("b", 1_180_000);
        store.claim("c", 1_180_000);
        assertTrue(store.size() <= 2);
        Map<String, Object> doc = AccessTokenVerifier.protectedResourceMetadata(API, ISSUER, null, true);
        assertEquals(true, doc.get("dpop_bound_access_tokens_required"));
        assertEquals(Dpop.ALGS, doc.get("dpop_signing_alg_values_supported"));
        assertEquals("DPoP error=\"use_dpop_nonce\", error_description=\"n\", algs=\"" + String.join(" ", Dpop.ALGS) + "\"",
                AccessTokenVerifier.buildWwwAuthenticate("DPoP", null, "use_dpop_nonce", "n", null, null));
    }

    @Test
    void springFilterDpopWithNonce() throws Exception {
        HmacDpopNonceSource nonces = new HmacDpopNonceSource("k".repeat(32).getBytes(StandardCharsets.UTF_8));
        AccessTokenVerifier v = verifier(b -> b.dpopNonces(nonces));
        AtlasAccessTokenFilter filter = new AtlasAccessTokenFilter(v);
        Key k = key("EdDSA");
        String tok = token(k.jkt());
        Function<String[], MockHttpServletResponse> send = proofs -> {
            MockHttpServletRequest req = new MockHttpServletRequest("PUT", "/docs/1");
            req.setScheme("https");
            req.setServerName("api.example.com");
            req.setServerPort(443);
            req.setQueryString("draft=1");
            req.addHeader("Authorization", "DPoP " + tok);
            for (String p : proofs) {
                req.addHeader("DPoP", p);
            }
            MockHttpServletResponse res = new MockHttpServletResponse();
            try {
                filter.doFilter(req, res, new MockFilterChain());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            return res;
        };
        Function<String[], String> p = a -> proof(k, tok, claims("htm", "PUT", "htu", "https://api.example.com/docs/1",
                "nonce", a[0], "jti", a[1]));
        MockHttpServletResponse first = send.apply(new String[] {p.apply(new String[] {null, "a"})});
        assertEquals(401, first.getStatus());
        String nonce = first.getHeader("DPoP-Nonce");
        assertNotNull(nonce);
        assertTrue(first.getHeader("WWW-Authenticate").startsWith("DPoP error=\"use_dpop_nonce\""));
        MockHttpServletResponse ok = send.apply(new String[] {p.apply(new String[] {nonce, "b"})});
        assertEquals(200, ok.getStatus());
        assertNotNull(ok.getHeader("DPoP-Nonce"));
        MockHttpServletResponse replay = send.apply(new String[] {p.apply(new String[] {nonce, "b"})});
        assertEquals(401, replay.getStatus());
        assertTrue(replay.getHeader("WWW-Authenticate").contains("error=\"invalid_dpop_proof\""));
        assertEquals(401, send.apply(new String[] {p.apply(new String[] {nonce, "c"}), p.apply(new String[] {nonce, "d"})}).getStatus());
    }
}
