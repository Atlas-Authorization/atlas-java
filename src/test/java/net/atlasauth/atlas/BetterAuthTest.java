package net.atlasauth.atlas;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import net.atlasauth.atlas.betterauth.BetterAuth;
import net.atlasauth.atlas.betterauth.BridgeCookie;
import net.atlasauth.atlas.betterauth.BridgeDecision;
import net.atlasauth.atlas.betterauth.ExchangeResult;
import net.atlasauth.atlas.betterauth.SessionBridge;
import net.atlasauth.atlas.models.BetterAuthBridgeSettings;
import net.atlasauth.atlas.models.BetterAuthImport;
import net.atlasauth.atlas.models.BetterAuthImportOptions;
import net.atlasauth.atlas.models.UpdateBetterAuthBridgeBody;
import net.atlasauth.atlas.resources.BetterAuthImports;
import net.atlasauth.atlas.verify.LegacyIssuer;
import net.atlasauth.atlas.verify.LegacyIssuerVerifier;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Deque;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Migrate from Better Auth: sealed-bundle imports (routes, line-aligned upload, wait), the bridge settings
 * and the secret-key exchange (classified outcomes), the request-level bridge, and Better Auth JWTs accepted
 * by the session verifier during the cutover.
 */
class BetterAuthTest {

    /** A loopback server that records every request and replies from a queue (default 200 {}). */
    static final class Recorder implements AutoCloseable {
        record Req(String method, String path, String query, Map<String, String> headers, String body) {
        }

        final HttpServer server;
        final List<Req> requests = new ArrayList<>();
        final Deque<String[]> replies = new ArrayDeque<>();

        Recorder() throws IOException {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", ex -> {
                Map<String, String> headers = new LinkedHashMap<>();
                ex.getRequestHeaders().forEach((k, v) -> headers.put(k.toLowerCase(), String.join(",", v)));
                String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                synchronized (this) {
                    requests.add(new Req(ex.getRequestMethod(), ex.getRequestURI().getPath(),
                            ex.getRequestURI().getRawQuery(), headers, body));
                }
                String[] reply = replies.isEmpty() ? new String[]{"200", "{}"} : replies.poll();
                byte[] out = reply[1].getBytes(StandardCharsets.UTF_8);
                ex.getResponseHeaders().add("content-type", "application/json");
                ex.sendResponseHeaders(Integer.parseInt(reply[0]), out.length == 0 ? -1 : out.length);
                if (out.length > 0) {
                    try (OutputStream os = ex.getResponseBody()) {
                        os.write(out);
                    }
                }
                ex.close();
            });
            server.start();
        }

        Recorder reply(int status, String body) {
            replies.add(new String[]{String.valueOf(status), body});
            return this;
        }

        String url() {
            return "http://127.0.0.1:" + server.getAddress().getPort();
        }

        Req last() {
            return requests.get(requests.size() - 1);
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }

    private Recorder api;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        api = new Recorder();
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(api.url()).build();
    }

    @AfterEach
    void tearDown() {
        api.close();
    }

    // ---- the 16 operations ---------------------------------------------------------------------

    @Test
    void everyOperationHitsItsRoute() {
        record Op(String method, String path, String query, String body, Runnable call) {
        }
        List<Op> ops = List.of(
                new Op("GET", "/v1/better_auth_imports/public_key", null, "", () -> atlas.betterAuthImports().getPublicKey()),
                new Op("POST", "/v1/better_auth_imports/public_key/rotate", null, "", () -> atlas.betterAuthImports().rotatePublicKey()),
                new Op("POST", "/v1/better_auth_imports", null, "{}", () -> atlas.betterAuthImports().create()),
                new Op("POST", "/v1/better_auth_imports", null, "{\"options\":{\"on_email_conflict\":\"link\",\"jwks_cutover_days\":7}}",
                        () -> atlas.betterAuthImports().create(new BetterAuthImportOptions().onEmailConflict("link").jwksCutoverDays(7L))),
                new Op("GET", "/v1/better_auth_imports", "limit=5&starting_after=bai_0", "", () -> atlas.betterAuthImports().list(5, "bai_0")),
                new Op("GET", "/v1/better_auth_imports/bai_1", null, "", () -> atlas.betterAuthImports().get("bai_1")),
                new Op("POST", "/v1/better_auth_imports/bai_1/finalize", null, "", () -> atlas.betterAuthImports().finalize("bai_1")),
                new Op("POST", "/v1/better_auth_imports/bai_1/dry_run", null, "{\"options\":{\"models\":[\"user\"]}}",
                        () -> atlas.betterAuthImports().dryRun("bai_1", new BetterAuthImportOptions().models(List.of("user")))),
                new Op("POST", "/v1/better_auth_imports/bai_1/dry_run", null, "{}", () -> atlas.betterAuthImports().dryRun("bai_1")),
                new Op("POST", "/v1/better_auth_imports/bai_1/apply", null, "{\"confirm_conflicts\":true}", () -> atlas.betterAuthImports().apply("bai_1", true)),
                new Op("POST", "/v1/better_auth_imports/bai_1/apply", null, "{}", () -> atlas.betterAuthImports().apply("bai_1")),
                new Op("DELETE", "/v1/better_auth_imports/bai_1", null, "", () -> atlas.betterAuthImports().delete("bai_1")),
                new Op("GET", "/v1/better_auth_imports/jwks", null, "", () -> atlas.betterAuthImports().listJwks()),
                new Op("DELETE", "/v1/better_auth_imports/jwks/k1", null, "", () -> atlas.betterAuthImports().deleteJwk("k1")),
                new Op("GET", "/v1/migrations/better_auth/bridge", null, "", () -> atlas.betterAuthBridge().get()),
                new Op("PUT", "/v1/migrations/better_auth/bridge", null,
                        "{\"enabled\":true,\"live_fallback\":{\"base_url\":\"https://old.test/api/auth\",\"secret\":null}}",
                        () -> atlas.betterAuthBridge().update(new UpdateBetterAuthBridgeBody().enabled(true)
                                .liveFallback(new UpdateBetterAuthBridgeBody.LiveFallback().baseUrl("https://old.test/api/auth").clearSecret()))),
                new Op("DELETE", "/v1/migrations/better_auth/bridge", null, "", () -> atlas.betterAuthBridge().delete()),
                new Op("POST", "/v1/migrations/better_auth/exchange", null, "{\"token\":\"tok.sig\"}", () -> atlas.betterAuthBridge().exchange("tok.sig")),
                new Op("POST", "/v1/migrations/better_auth/exchange", null, "{\"token\":\"t\",\"remember\":false}",
                        () -> atlas.betterAuthBridge().exchange("t", null, null, false)));
        for (Op op : ops) {
            op.call().run();
            Recorder.Req r = api.last();
            assertEquals(op.method(), r.method(), op.path());
            assertEquals(op.path(), r.path());
            assertEquals(op.query(), r.query(), op.path());
            assertEquals(op.body(), r.body(), op.method() + " " + op.path());
            assertEquals("Bearer sk_test_123", r.headers().get("authorization"));
        }
    }

    @Test
    void typedResponsesDecode() {
        api.reply(200, "{\"object\":\"better_auth_import\",\"id\":\"bai_1\",\"status\":\"ready\",\"key_id\":\"bik_1\","
                + "\"progress\":{\"percent\":40},\"report\":{\"models\":[{\"model\":\"user\",\"found\":3,\"will_create\":2}],"
                + "\"conflicts_total\":1,\"action_items\":[{\"code\":\"stripe_key_missing\",\"severity\":\"required\",\"count\":2}]},"
                + "\"error\":null,\"created_at\":1}");
        BetterAuthImport imp = atlas.betterAuthImports().get("bai_1");
        assertEquals("ready", imp.status());
        assertEquals("bik_1", imp.keyId());
        assertEquals(40L, imp.progress().percent());
        assertEquals(2L, imp.report().models().get(0).willCreate());
        assertEquals("required", imp.report().actionItems().get(0).severity());
        assertFalse(imp.inFlight());
        api.reply(200, "{\"object\":\"better_auth_bridge\",\"enabled\":true,\"ends_at\":1800000000000,"
                + "\"live_fallback\":{\"enabled\":true,\"base_url\":\"https://old.test\",\"has_secret\":true},\"stats\":{\"from_import\":4}}");
        BetterAuthBridgeSettings s = atlas.betterAuthBridge().get();
        assertTrue(s.liveFallback().hasSecret());
        assertEquals(4L, s.stats().fromImport());
        assertEquals(1800000000000L, s.endsAt());
    }

    @Test
    void uploadPartIsTextPlainAndExchangeForwardsTheEndUser() {
        atlas.betterAuthImports().uploadPart("bai_1", 2, "line-a\nline-b\n");
        Recorder.Req r = api.last();
        assertEquals("PUT", r.method());
        assertEquals("/v1/better_auth_imports/bai_1/parts/2", r.path());
        assertEquals("text/plain; charset=utf-8", r.headers().get("content-type"));
        assertEquals("line-a\nline-b\n", r.body());

        atlas.betterAuthBridge().exchange("tok", "203.0.113.9", "Mozilla/5.0 test", null);
        r = api.last();
        assertEquals("203.0.113.9", r.headers().get("x-atlas-client-ip"));
        assertEquals("Mozilla/5.0 test", r.headers().get("x-atlas-client-user-agent"));
        assertEquals("Bearer sk_test_123", r.headers().get("authorization"));
        atlas.betterAuthBridge().exchange("tok");
        assertNull(api.last().headers().get("x-atlas-client-ip"));

        // A caller-supplied header never replaces the Bearer secret.
        atlas.transport().requestWithHeaders("GET", "/v1/x", null, null, null,
                Map.of("Authorization", "Bearer evil", "X-Extra", "y"), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                });
        assertEquals("Bearer sk_test_123", api.last().headers().get("authorization"));
        assertEquals("y", api.last().headers().get("x-extra"));
    }

    // ---- bundle upload ----------------------------------------------------------------------

    @Test
    void splitBundlePartsIsLineAligned() {
        assertEquals(List.of(), BetterAuthImports.splitBundleParts("\n\r\n", 10));
        assertEquals(List.of("a\nb\n"), BetterAuthImports.splitBundleParts("a\r\nb", 10));
        assertEquals(List.of("aaa\n", "bb\ncc\n", "dddddddd\n", "e\n"),
                BetterAuthImports.splitBundleParts("aaa\nbb\ncc\ndddddddd\ne\n", 6));
        assertEquals(List.of("é\n", "é\n"), BetterAuthImports.splitBundleParts("é\né\n", 4), "UTF-8 bytes, not chars");
        for (String part : BetterAuthImports.splitBundleParts("0123456789\n".repeat(1000), 1000)) {
            assertTrue(part.getBytes(StandardCharsets.UTF_8).length <= 1000);
            assertTrue(part.endsWith("\n"));
        }
        assertEquals(8 * 1024 * 1024, BetterAuthImports.PART_BYTES);
        assertThrows(IllegalArgumentException.class, () -> BetterAuthImports.splitBundleParts("x", 0));
    }

    @Test
    void uploadCreatesUploadsEveryPartAndFinalizes() {
        api.reply(201, "{\"object\":\"better_auth_import\",\"id\":\"bai_9\",\"status\":\"uploading\"}")
                .reply(200, "{\"part\":1}").reply(200, "{\"part\":2}").reply(200, "{\"part\":3}")
                .reply(200, "{\"id\":\"bai_9\",\"status\":\"ready\"}");
        List<String> seen = new ArrayList<>();
        BetterAuthImport done = atlas.betterAuthImports().upload("h1\nr1\nr2x\nr3\n",
                new BetterAuthImportOptions().importSessions(false), 6, (p, total) -> seen.add(p + "/" + total));
        assertEquals("ready", done.status());
        assertEquals(List.of("1/3", "2/3", "3/3"), seen);
        List<Recorder.Req> r = api.requests;
        assertEquals(5, r.size());
        assertEquals("{\"options\":{\"import_sessions\":false}}", r.get(0).body());
        assertEquals("/v1/better_auth_imports/bai_9/parts/1", r.get(1).path());
        assertEquals("h1\nr1\n", r.get(1).body());
        assertEquals("r2x\n", r.get(2).body());
        assertEquals("r3\n", r.get(3).body());
        assertEquals("/v1/better_auth_imports/bai_9/finalize", r.get(4).path());

        int before = api.requests.size();
        assertThrows(IllegalArgumentException.class, () -> atlas.betterAuthImports().upload("\n"));
        assertEquals(before, api.requests.size());
    }

    @Test
    void waitForPollsUntilTheImportSettles() {
        api.reply(200, "{\"status\":\"analyzing\"}").reply(200, "{\"status\":\"applying\"}").reply(200, "{\"status\":\"completed\"}");
        List<Duration> sleeps = new ArrayList<>();
        BetterAuthImports imports = new BetterAuthImports(atlas.transport(), sleeps::add);
        List<String> statuses = new ArrayList<>();
        BetterAuthImport done = imports.waitFor("bai_1", Duration.ofMillis(500), Duration.ofMinutes(1), i -> statuses.add(i.status()));
        assertEquals("completed", done.status());
        assertEquals(List.of("analyzing", "applying", "completed"), statuses);
        assertEquals(List.of(Duration.ofMillis(500), Duration.ofMillis(500)), sleeps);

        for (int i = 0; i < 4; i++) {
            api.reply(200, "{\"status\":\"queued\"}");
        }
        BetterAuthImports stuck = new BetterAuthImports(atlas.transport(), d -> {
        });
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> stuck.waitFor("bai_2", Duration.ofSeconds(1), Duration.ofSeconds(2), null));
        assertTrue(e.getMessage().contains("Timed out"));
    }

    // ---- classified exchange ----------------------------------------------------------------

    @Test
    void exchangeIsClassified() throws IOException {
        api.reply(200, "{\"object\":\"session_tokens\",\"jwt\":\"j\",\"refresh_token\":\"r\",\"session_id\":\"sess_1\",\"user_id\":\"user_1\",\"expires_in\":60}")
                .reply(200, "{\"object\":\"sign_in_attempt\",\"id\":\"sia_1\",\"status\":\"needs_second_factor\"}")
                .reply(400, "{\"errors\":[{\"code\":\"VERIFICATION_FAILED\",\"message\":\"x\",\"meta\":{\"bridge_outcome\":\"reused\"}}]}")
                .reply(403, "{\"errors\":[{\"code\":\"FORBIDDEN\",\"message\":\"x\",\"meta\":{\"bridge_outcome\":\"closed\"}}]}")
                .reply(429, "{\"errors\":[{\"code\":\"RATE_LIMITED\",\"message\":\"slow\"}]}")
                .reply(503, "upstream")
                .reply(403, "{\"errors\":[{\"code\":\"IP_BLOCKED\",\"message\":\"x\"}]}")
                .reply(200, "{\"object\":\"sign_in_attempt\",\"id\":\"sia_2\",\"status\":\"complete\"}");

        ExchangeResult s = BetterAuth.exchange(atlas, "tok", "198.51.100.1", "UA");
        assertEquals(ExchangeResult.Kind.SESSION, s.kind());
        assertEquals(List.of("j", "r", "sess_1", "user_1"), List.of(s.jwt(), s.refreshToken(), s.sessionId(), s.userId()));
        assertEquals(60, s.expiresIn());
        ExchangeResult m = BetterAuth.exchange(atlas, "tok", null, null);
        assertEquals(ExchangeResult.Kind.NEEDS_MFA, m.kind());
        assertEquals("sia_1", m.attemptId());
        ExchangeResult r = BetterAuth.exchange(atlas, "tok", null, null);
        assertEquals("reused", r.outcome());
        assertEquals(400, r.httpStatus());
        assertTrue(r.isFinalRefusal());
        r = BetterAuth.exchange(atlas, "tok", null, null);
        assertEquals("closed", r.outcome());
        assertFalse(r.isFinalRefusal());
        r = BetterAuth.exchange(atlas, "tok", null, null);
        assertEquals("rate_limited", r.outcome());
        assertEquals(429, r.httpStatus());
        r = BetterAuth.exchange(atlas, "tok", null, null);
        assertEquals("unavailable", r.outcome());
        assertEquals(503, r.httpStatus());
        r = BetterAuth.exchange(atlas, "tok", null, null);
        assertEquals("unavailable", r.outcome());
        assertEquals(403, r.httpStatus());
        r = BetterAuth.exchange(atlas, "tok", null, null);
        assertEquals(ExchangeResult.Kind.REFUSED, r.kind());
        assertEquals("unavailable", r.outcome());
        assertEquals(200, r.httpStatus());

        // No server at all: a transport failure keeps the cookie.
        String dead;
        try (Recorder gone = new Recorder()) {
            dead = gone.url();
        }
        AtlasClient offline = AtlasClient.builder().secretKey("sk_test_123").apiUrl(dead).timeout(Duration.ofSeconds(2)).build();
        r = BetterAuth.exchange(offline, "tok", null, null);
        assertEquals("unavailable", r.outcome());
        assertEquals(0, r.httpStatus());
    }

    // ---- the request-level bridge -----------------------------------------------------------

    @Test
    void cookieNamesAndReading() {
        assertEquals(List.of("__Secure-better-auth.session_token", "better-auth.session_token"),
                BetterAuth.sessionCookieNames("better-auth"));
        assertEquals(8, BetterAuth.allCookieNames("better-auth").size());
        assertEquals("sec", BetterAuth.readSessionCookie("better-auth.session_token=plain; __Secure-better-auth.session_token=sec"));
        assertEquals("plain", BetterAuth.readSessionCookie("a=1; better-auth.session_token=plain"));
        assertEquals("v", BetterAuth.readSessionCookie(Map.of("myapp.session_token", "v"), "myapp", null));
        assertEquals("w", BetterAuth.readSessionCookie(Map.of("sid", "w"), null, "sid"));
        assertNull(BetterAuth.readSessionCookie("x=1"));
        assertEquals("a=b; Path=/; Domain=.example.com; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; Secure; SameSite=Lax",
                new BridgeCookie("a", "b", "/", ".example.com", true, "Lax", true, 0).toHeader());
    }

    @Test
    void bridgeDecisions() {
        Deque<ExchangeResult> results = new ArrayDeque<>(List.of(
                ExchangeResult.session("jwt1", "rt1", "s", "u", 60),
                ExchangeResult.needsMfa("sia_1", "needs_mfa_enrollment"),
                ExchangeResult.refused("expired", 400),
                ExchangeResult.refused("closed", 403),
                ExchangeResult.refused("rate_limited", 429)));
        List<String> calls = new ArrayList<>();
        SessionBridge bridge = SessionBridge.builder(atlas).betterAuthCookieDomain(".example.com")
                .exchanger((c, token, ip, ua) -> {
                    calls.add(token + "|" + ip + "|" + ua);
                    return results.poll();
                }).build();

        assertEquals(BridgeDecision.Action.NONE, bridge.decide(Map.of("a", "1"), null, null).action());
        assertEquals(BridgeDecision.Action.NONE, bridge.decide(Map.of("better-auth.session_token", "t", "__session", "x"), null, null).action());
        assertEquals(BridgeDecision.Action.NONE, bridge.decide(Map.of("better-auth.session_token", "t", "__atlas_rt", "y"), null, null).action());
        assertTrue(calls.isEmpty());

        BridgeDecision d = bridge.decide(Map.of("better-auth.session_token", "tok.sig"), "192.0.2.1", "UA/1");
        assertEquals(BridgeDecision.Action.SIGNED_IN, d.action());
        assertEquals(List.of("tok.sig|192.0.2.1|UA/1"), calls);
        List<String> headers = d.setCookieHeaders();
        assertEquals("__session=jwt1; Path=/; Secure; SameSite=Lax", headers.get(0));
        assertEquals("__atlas_rt=rt1; Path=/; HttpOnly; Secure; SameSite=Lax", headers.get(1));
        assertEquals(10, headers.size());
        assertTrue(headers.contains("__Secure-better-auth.session_token=; Path=/; Domain=.example.com; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure"));
        assertTrue(headers.contains("better-auth.session_token=; Path=/; Domain=.example.com; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT"));

        d = bridge.decide(Map.of("better-auth.session_token", "t2"), null, null);
        assertEquals(BridgeDecision.Action.NEEDS_MFA, d.action());
        assertEquals(8, d.cookies().size());
        assertEquals("/sign-in?next=%2Fx&__atlas_attempt=sia_1&__atlas_status=needs_mfa_enrollment#top",
                d.continuationUrl("/sign-in?next=%2Fx&__atlas_attempt=old&__atlas_status=old#top"));

        d = bridge.decide(Map.of("better-auth.session_token", "t3"), null, null);
        assertEquals("expired", d.outcome());
        assertEquals(8, d.cookies().size());
        d = bridge.decide(Map.of("better-auth.session_token", "t4"), null, null);
        assertEquals("closed", d.outcome());
        assertEquals(0, d.cookies().size());
        d = bridge.decide(Map.of("better-auth.session_token", "t5"), null, null);
        assertEquals("rate_limited", d.outcome());
        assertEquals(0, d.cookies().size());

        SessionBridge custom = SessionBridge.builder(atlas).cookieName("sid").secure(false)
                .exchanger((c, token, ip, ua) -> ExchangeResult.session("j", "r", "", "", 0)).build();
        d = custom.decide(Map.of("sid", "abc"), null, null);
        assertEquals("__session=j; Path=/; SameSite=Lax", d.setCookieHeaders().get(0));
        assertEquals(12, d.cookies().size());

        // The default exchanger goes over the Backend API.
        api.reply(200, "{\"object\":\"session_tokens\",\"jwt\":\"jA\",\"refresh_token\":\"rA\"}");
        d = SessionBridge.builder(atlas).build().decide(Map.of("__Secure-better-auth.session_token", "live"), "203.0.113.4", "UA");
        assertEquals(BridgeDecision.Action.SIGNED_IN, d.action());
        assertEquals("{\"token\":\"live\"}", api.last().body());
        assertEquals("203.0.113.4", api.last().headers().get("x-atlas-client-ip"));
    }

    // ---- Better Auth JWTs during the cutover ------------------------------------------------

    private static final String OLD = "https://old-app.test";
    private static final String ATLAS_ISSUER = "https://issuer.test";
    private static final long NOW = 1_700_000_000_000L;

    /** A signer + public JWK (as a map) for one algorithm. */
    private record Key(String alg, String kid, Function<byte[], byte[]> rawSign, JWSSigner signer, Map<String, Object> jwk) {
    }

    private static Key keyFor(String alg, String kid) throws Exception {
        switch (alg) {
            case "RS256", "PS256" -> {
                RSAKey k = new RSAKeyGenerator(2048).keyID(kid).generate();
                return new Key(alg, kid, null, new RSASSASigner(k), k.toPublicJWK().toJSONObject());
            }
            case "ES256", "ES512" -> {
                ECKey k = new ECKeyGenerator("ES256".equals(alg) ? Curve.P_256 : Curve.P_521).keyID(kid).generate();
                return new Key(alg, kid, null, new ECDSASigner(k), k.toPublicJWK().toJSONObject());
            }
            default -> {
                KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
                byte[] spki = pair.getPublic().getEncoded();
                byte[] x = java.util.Arrays.copyOfRange(spki, spki.length - 32, spki.length);
                Map<String, Object> jwk = new LinkedHashMap<>();
                jwk.put("kty", "OKP");
                jwk.put("crv", "Ed25519");
                jwk.put("x", Base64.getUrlEncoder().withoutPadding().encodeToString(x));
                jwk.put("kid", kid);
                jwk.put("alg", "EdDSA");
                return new Key(alg, kid, in -> {
                    try {
                        Signature s = Signature.getInstance("Ed25519");
                        s.initSign(pair.getPrivate());
                        s.update(in);
                        return s.sign();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }, null, jwk);
            }
        }
    }

    private static String b64(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private static String token(Key key, String headerAlg, Map<String, Object> claims) throws Exception {
        if (key.signer() != null && headerAlg.equals(key.alg())) {
            JWTClaimsSet.Builder c = new JWTClaimsSet.Builder();
            claims.forEach((k, v) -> c.claim(k, v instanceof Long l && (k.equals("exp") || k.equals("nbf") || k.equals("iat")) ? new Date(l * 1000) : v));
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.parse(headerAlg)).keyID(key.kid()).type(JOSEObjectType.JWT).build(), c.build());
            jwt.sign(key.signer());
            return jwt.serialize();
        }
        String input = b64(("{\"alg\":\"" + headerAlg + "\",\"kid\":\"" + key.kid() + "\",\"typ\":\"JWT\"}").getBytes(StandardCharsets.UTF_8))
                + "." + b64(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsBytes(claims));
        byte[] sig = key.rawSign() != null ? key.rawSign().apply(input.getBytes(StandardCharsets.US_ASCII)) : new byte[]{1, 2, 3};
        return input + "." + b64(sig);
    }

    private SessionVerifier legacyVerifier(List<LegacyIssuer> issuers, String secretKey, long now) {
        return SessionVerifier.builder()
                .jwksUrl(api.url() + "/v1/jwks")
                .issuer(ATLAS_ISSUER)
                .authorizedParties(List.of("https://app.test"))
                .secretKey(secretKey)
                .bapiBaseUrl(api.url())
                .now(() -> now)
                .legacyIssuers(issuers)
                .build();
    }

    private static Map<String, Object> claims(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    @Test
    void legacyTokensOfEveryBetterAuthAlgorithmVerify() throws Exception {
        for (String alg : LegacyIssuer.BETTER_AUTH_ALGS) {
            Key key = keyFor(alg, "k-" + alg);
            api.reply(200, "{\"data\":[{\"id\":\"user_atlas_1\"}]}");
            SessionVerifier v = legacyVerifier(List.of(LegacyIssuer.builder(OLD).untilMillis(NOW + 86_400_000L)
                    .jwks(Map.of("keys", List.of(key.jwk()))).build()), "sk_test_lookup", NOW);
            String tok = token(key, alg, claims("iss", OLD, "sub", "ba_user_1", "exp", NOW / 1000 + 600, "email", "a@b.test"));
            VerifyResult res = v.verify(tok);
            assertTrue(res.ok(), alg + " should verify");
            assertEquals("user_atlas_1", res.claims().sub());
            assertEquals("ba_user_1", res.claims().get("external_id"));
            assertEquals(Map.of("kind", "better_auth", "issuer", OLD, "sub", "ba_user_1"), res.claims().get("legacy"));
            assertEquals("a@b.test", res.claims().get("email"));
            assertEquals(NOW / 1000 + 600, res.claims().exp());
            Recorder.Req lookup = api.last();
            assertEquals("/v1/users", lookup.path());
            assertEquals("external_id=ba_user_1&limit=1", lookup.query());
            assertEquals("Bearer sk_test_lookup", lookup.headers().get("authorization"));

            String[] parts = tok.split("\\.");
            String forged = parts[0] + "." + b64(("{\"iss\":\"" + OLD + "\",\"sub\":\"ba_admin\",\"exp\":" + (NOW / 1000 + 600) + "}")
                    .getBytes(StandardCharsets.UTF_8)) + "." + parts[2];
            assertEquals(VerifyResult.Reason.INVALID, v.verify(forged).reason(), alg + " forged");
        }
    }

    @Test
    void legacyRulesWindowExpiryAudienceAndKeys() throws Exception {
        Key key = keyFor("EdDSA", "ed1");
        long until = NOW + 3_600_000L;
        api.reply(200, "{\"data\":[{\"id\":\"user_2\"}]}");
        SessionVerifier v = legacyVerifier(List.of(LegacyIssuer.builder(OLD).untilMillis(until)
                .jwks(Map.of("keys", List.of(key.jwk()))).audience("https://api.old.test").build()), "sk", NOW);
        long exp = NOW / 1000 + 60;

        assertTrue(v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test", "exp", exp))).ok());
        assertTrue(v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", List.of("https://api.old.test"), "exp", exp, "jti", "again"))).ok());
        assertEquals(1, api.requests.size(), "the sub mapping is cached");

        assertEquals(VerifyResult.Reason.INVALID, v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test"))).reason(), "exp is required");
        assertEquals(VerifyResult.Reason.INVALID, v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test", "exp", NOW / 1000 - 10))).reason(), "expired");
        assertTrue(v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test", "exp", NOW / 1000 - 3))).ok(), "leeway");
        assertEquals(VerifyResult.Reason.INVALID, v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test", "exp", exp, "nbf", NOW / 1000 + 60))).reason(), "nbf");
        assertEquals(VerifyResult.Reason.INVALID, v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://elsewhere.test", "exp", exp))).reason(), "audience");
        assertEquals(VerifyResult.Reason.UNAUTHORIZED_PARTY, v.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test", "exp", exp, "azp", "https://evil.test"))).reason());
        assertEquals(VerifyResult.Reason.INVALID, v.verify(token(key, "HS256", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test", "exp", exp))).reason(), "HS256 never");
        Key other = keyFor("EdDSA", "ed-other");
        assertEquals(VerifyResult.Reason.INVALID, v.verify(token(other, "EdDSA", claims("iss", OLD, "sub", "ba_2", "aud", "https://api.old.test", "exp", exp))).reason(), "unknown kid");
        assertEquals("ba_2", v.verify(token(key, "EdDSA", claims("iss", OLD, "id", "ba_2", "aud", "https://api.old.test", "exp", exp))).claims().get("external_id"), "id stands in for sub");

        // After the window the issuer is foreign: the Atlas path refuses it.
        api.reply(200, "{\"keys\":[]}");
        SessionVerifier after = legacyVerifier(List.of(LegacyIssuer.builder(OLD).untilMillis(until)
                .jwks(Map.of("keys", List.of(key.jwk()))).build()), "sk", NOW + 7_200_000L);
        assertFalse(after.verify(token(key, "EdDSA", claims("iss", OLD, "sub", "ba_2", "exp", NOW / 1000 + 9000))).ok());
    }

    @Test
    void legacyJwksUrlSubjectFailuresAndResolver() throws Exception {
        Key key = keyFor("ES256", "ec1");
        String jwks = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(Map.of("keys", List.of(key.jwk())));
        api.reply(200, jwks).reply(500, "down").reply(200, "{\"data\":[]}");
        SessionVerifier v = legacyVerifier(List.of(LegacyIssuer.builder(OLD).until("2099-01-01T00:00:00Z")
                .jwksUrl(api.url() + "/api/auth/jwks").build()), "sk", NOW);
        String tok = token(key, "ES256", claims("iss", OLD, "sub", "ba_3", "exp", NOW / 1000 + 60));
        assertEquals(VerifyResult.Reason.INVALID, v.verify(tok).reason(), "a failed lookup refuses");
        assertEquals("/api/auth/jwks", api.requests.get(0).path());
        assertEquals(VerifyResult.Reason.INVALID, v.verify(tok).reason(), "failure not cached; no such user");
        assertEquals(3, api.requests.size());
        assertEquals(VerifyResult.Reason.INVALID, v.verify(tok).reason(), "a miss is cached");
        assertEquals(3, api.requests.size());

        try (Recorder down = new Recorder()) {
            down.reply(500, "down");
            SessionVerifier noKeys = legacyVerifier(List.of(LegacyIssuer.builder(OLD).until("2099-01-01")
                    .jwksUrl(down.url() + "/jwks").build()), "sk", NOW);
            assertEquals(VerifyResult.Reason.NO_KEYS, noKeys.verify(tok).reason());
        }

        List<String> seen = new ArrayList<>();
        SessionVerifier mapped = legacyVerifier(List.of(LegacyIssuer.builder(OLD).until(Instant.parse("2099-01-01T00:00:00Z"))
                .jwks(Map.of("keys", List.of(key.jwk())))
                .resolveSubject((sub, c) -> {
                    seen.add(sub);
                    return "ba_3".equals(sub) ? "user_mapped" : null;
                }).build()), null, NOW);
        assertEquals("user_mapped", mapped.verify(tok).claims().sub());
        assertEquals(VerifyResult.Reason.INVALID, mapped.verify(token(key, "ES256", claims("iss", OLD, "sub", "ba_x", "exp", NOW / 1000 + 60))).reason());
        assertEquals(List.of("ba_3", "ba_x"), seen);
    }

    @Test
    void legacyConfigurationErrors() {
        Map<String, Object> jwks = Map.of("keys", List.of());
        List<Runnable> bad = List.of(
                () -> LegacyIssuer.builder("").until("2099-01-01").jwks(jwks).build(),
                () -> LegacyIssuer.builder(OLD).until("2099-01-01").build(),
                () -> LegacyIssuer.builder(OLD).until("2099-01-01").jwks(jwks).jwksUrl("https://x.test").build(),
                () -> LegacyIssuer.builder(OLD).jwks(jwks).build(),
                () -> LegacyIssuer.builder(OLD).until("not a date").jwks(jwks).build(),
                () -> LegacyIssuer.builder(OLD).until("2099-01-01").jwks(jwks).algorithms(List.of("HS256")).build(),
                () -> legacyVerifier(List.of(LegacyIssuer.builder(ATLAS_ISSUER).until("2099-01-01").jwks(jwks).build()), "sk", NOW),
                () -> legacyVerifier(List.of(LegacyIssuer.builder(OLD).until("2099-01-01").jwks(jwks).build()), null, NOW));
        for (int i = 0; i < bad.size(); i++) {
            int n = i;
            assertThrows(IllegalArgumentException.class, () -> bad.get(n).run(), "case " + n);
        }
        assertEquals(4_070_908_800_000L, LegacyIssuer.parseUntil("2099-01-01T00:00:00Z"));
        assertEquals(4_070_908_800_000L, LegacyIssuer.parseUntil("2099-01-01"));
        assertEquals(4_070_908_800_000L, LegacyIssuer.parseUntil("2099-01-01T02:00:00+02:00"));
        assertEquals(OLD, LegacyIssuerVerifier.peekIssuer(b64("{}".getBytes()) + "." + b64(("{\"iss\":\"" + OLD + "\"}").getBytes()) + ".x"));
        assertNull(LegacyIssuerVerifier.peekIssuer("nope"));
    }

    @Test
    void atlasTokensStillVerifyAlongsideLegacyIssuers() throws Exception {
        RSAKey atlasKey = new RSAKeyGenerator(2048).keyID("atlas-1").generate();
        api.reply(200, new com.nimbusds.jose.jwk.JWKSet(atlasKey.toPublicJWK()).toString());
        Key old = keyFor("EdDSA", "ed1");
        SessionVerifier v = legacyVerifier(List.of(LegacyIssuer.builder(OLD).until("2099-01-01")
                .jwks(Map.of("keys", List.of(old.jwk()))).build()), "sk", NOW);
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("atlas-1").build(),
                new JWTClaimsSet.Builder().issuer(ATLAS_ISSUER).subject("user_9").claim("azp", "https://app.test")
                        .expirationTime(new Date(NOW + 60_000)).build());
        jwt.sign(new RSASSASigner(atlasKey));
        VerifyResult res = v.verify(jwt.serialize());
        assertTrue(res.ok());
        assertEquals("user_9", res.claims().sub());
        assertNull(res.claims().get("legacy"));
    }
}
