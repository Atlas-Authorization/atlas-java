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
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.atlasauth.atlas.operator.OperatorLogin;
import net.atlasauth.atlas.privacy.PrivacyCall;
import net.atlasauth.atlas.privacy.PrivacyCallVerifier;
import net.atlasauth.atlas.verify.BackchannelLogoutHandler;
import net.atlasauth.atlas.verify.LogoutTokenVerifier;
import net.atlasauth.atlas.webhook.WebhookEvents;
import net.atlasauth.atlas.webhook.WebhookVerification;
import net.atlasauth.atlas.webhook.WebhookVerifier;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Key formats, trace context, v2 webhook signatures, event aliases, the privacy-call verifier, the operator flow and the back-channel logout handler. */
class SpHelpersTest {

    // ---- traceparent ---------------------------------------------------------

    @Test
    void traceparentRoundTrip() {
        String tp = Traceparent.create();
        Traceparent.Parts parts = Traceparent.parse(tp);
        assertNotNull(parts);
        assertEquals(32, parts.traceId().length());
        assertEquals(16, parts.spanId().length());
        assertEquals("01", parts.flags());

        Traceparent.Parts good = Traceparent.parse("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");
        assertEquals("4bf92f3577b34da6a3ce929d0e0e4736", good.traceId());
        assertEquals("00f067aa0ba902b7", good.spanId());
        for (String bad : new String[]{"", "xx", "00-" + "0".repeat(32) + "-00f067aa0ba902b7-01", "00-4bf92f3577b34da6a3ce929d0e0e4736-" + "0".repeat(16) + "-01"}) {
            assertNull(Traceparent.parse(bad), bad);
        }
        assertNull(Traceparent.parse(null));
    }

    @Test
    void traceparentFromHeaders() {
        String good = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";
        assertEquals(good, Traceparent.fromHeaders(Map.of("Traceparent", good)));
        assertEquals(good, Traceparent.fromHeaders(Map.of("traceparent", List.of(good))));
        assertNull(Traceparent.fromHeaders(Map.of("traceparent", "junk")));
        assertNull(Traceparent.fromHeaders(Map.of()));
        assertNull(Traceparent.fromHeaders(null));
    }

    // ---- key formats ---------------------------------------------------------

    @Test
    void keyFormatsAcceptBothSpellings() {
        assertTrue(KeyFormat.isSecretKey("atlas_live_sk_abc123"));
        assertTrue(KeyFormat.isSecretKey("atlas_test_sk_abc123"));
        assertTrue(KeyFormat.isSecretKey("sk_live_abc"));
        assertTrue(KeyFormat.isSecretKey("sk_test_abc"));
        assertFalse(KeyFormat.isSecretKey("sk_abc"));
        assertFalse(KeyFormat.isSecretKey("pk_live_abc"));
        assertTrue(KeyFormat.isPlatformKey("atlas_live_psk_abc"));
        assertTrue(KeyFormat.isPlatformKey("psk_abc"));
        assertTrue(KeyFormat.isPlatformKey("psk_live_abc"));
        assertTrue(KeyFormat.isPublishableKey("pk_live_abc"));
        assertFalse(KeyFormat.isPublishableKey("atlas_live_pk_abc"));
        assertTrue(KeyFormat.isWebhookSecret("whsec_abc"));
        assertTrue(KeyFormat.isWebhookSecret("atlas_live_whsec_abc"));
        assertTrue(KeyFormat.isPartnerLinkSecret("atlas_live_pl_abc"));
        assertFalse(KeyFormat.isPartnerLinkSecret("pl_abc"));
        assertTrue(KeyFormat.isAccessToken("atlas_live_at_abc"));
        assertNull(KeyFormat.parse("atlas_prod_sk_abc"));
        assertNull(KeyFormat.parse("atlas_live_zz_abc"));
        assertNull(KeyFormat.parse("nope"));
        assertNull(KeyFormat.parse(null));
        KeyFormat.Parsed p = KeyFormat.parse("atlas_test_sk_abc");
        assertEquals(KeyFormat.Kind.SECRET, p.kind());
        assertEquals("test", p.environment());
        assertTrue(p.suite());
        KeyFormat.Parsed legacy = KeyFormat.parse("sk_live_abc");
        assertFalse(legacy.suite());
        assertEquals("live", legacy.environment());
        assertNull(KeyFormat.parse("psk_abc").environment());
    }

    @Test
    void clientAcceptsTheSuiteKeyFormat() throws IOException {
        try (MockBackend mock = MockBackend.start()) {
            AtlasClient atlas = AtlasClient.builder().secretKey("atlas_live_sk_abc123").apiUrl(mock.baseUrl()).build();
            atlas.users().list();
            assertEquals("Bearer atlas_live_sk_abc123", mock.authorization);
        }
    }

    // ---- v2 webhook signatures ----------------------------------------------

    private static String hmac(String secret, String prefix, String body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        mac.update(prefix.getBytes(StandardCharsets.UTF_8));
        mac.update(body.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(mac.doFinal());
    }

    private static Map<String, String> signed(String secret, String id, long ts, String url, String body, boolean v1, boolean v2, String extra) throws Exception {
        List<String> entries = new ArrayList<>();
        if (v1) entries.add("v1," + hmac(secret, id + "." + ts + ".", body));
        if (v2) entries.add("v2," + hmac(secret, id + "." + ts + "." + url + ".", body));
        if (extra != null) entries.add(extra);
        Map<String, String> h = new LinkedHashMap<>();
        h.put("atlas-id", id);
        h.put("atlas-timestamp", Long.toString(ts));
        h.put("atlas-signature", String.join(" ", entries));
        return h;
    }

    @Test
    void v2SignaturesBindTheUrl() throws Exception {
        String secret = "atlas_live_whsec_secretvalue";
        String url = "https://app.example.com/hooks/atlas";
        String body = "{\"id\":\"evt_1\",\"type\":\"user.created\",\"data\":{}}";
        long now = 1_800_000_000_000L;
        WebhookVerifier verifier = new WebhookVerifier(secret);
        byte[] raw = body.getBytes(StandardCharsets.UTF_8);

        // v1 + v2: verifies as v2 with the right URL
        Map<String, String> both = signed(secret, "evt_1", now, url, body, true, true, "v3,ignored");
        WebhookVerification ok = verifier.verify(raw, both, now, url, true);
        assertTrue(ok.verified(), ok.reason());
        assertEquals("evt_1", ok.deliveryId());
        assertEquals("user.created", ok.event().get("type").asText());

        // a replay to another URL fails the v2 check
        assertEquals("signature_mismatch", verifier.verify(raw, both, now, "https://evil.example.com/hook", true).reason());
        // v1-only delivery: fine without requireV2, refused with it
        Map<String, String> v1only = signed(secret, "evt_1", now, url, body, true, false, null);
        assertTrue(verifier.verify(raw, v1only, now, url, false).verified());
        assertEquals("malformed_signature", verifier.verify(raw, v1only, now, url, true).reason());
        // v2-only works for a verifier that knows the URL, and is malformed for one that does not
        Map<String, String> v2only = signed(secret, "evt_1", now, url, body, false, true, null);
        assertTrue(verifier.verify(raw, v2only, now, url, true).verified());
        assertEquals("malformed_signature", verifier.verify(raw, v2only, now).reason());
        // unknown versions only
        Map<String, String> unknown = signed(secret, "evt_1", now, url, body, false, false, "v9,abc");
        assertEquals("malformed_signature", verifier.verify(raw, unknown, now, url, false).reason());
        // stale
        assertEquals("stale_timestamp", verifier.verify(raw, both, now + 10 * 60_000L, url, true).reason());
        // the two-argument verify keeps its v1 behaviour on a delivery that also carries v2
        assertTrue(verifier.verify(raw, both, now).verified());
    }

    // ---- event names ---------------------------------------------------------

    @Test
    void eventAliases() {
        assertEquals("organization_membership.created", WebhookEvents.canonical("organizationMembership.created"));
        assertEquals("user.created", WebhookEvents.canonical("user.created"));
        assertTrue(WebhookEvents.isDeprecated("session.ended"));
        assertFalse(WebhookEvents.isDeprecated("session.revoked"));
        assertEquals(List.of("session.revoked", "session.ended", "session.removed"), WebhookEvents.spellings("session.ended"));
        assertEquals(List.of("organization_membership.deleted", "organization.membership.deleted", "organizationMembership.deleted"),
                WebhookEvents.spellings("organization_membership.deleted"));
        assertEquals("rename", WebhookEvents.ALIASES.get("appRole.created").kind());
        assertEquals("companion", WebhookEvents.ALIASES.get("user.deletion_cancelled").kind());
        assertEquals("webhook_endpoint.failing", WebhookEvents.WEBHOOK_ENDPOINT_FAILING);
        assertEquals("secret_key.expiring", WebhookEvents.SECRET_KEY_EXPIRING);
        assertEquals("referalli", AtlasConstants.REFERRAL_PROVIDER_REFERALLI);
    }

    // ---- privacy calls -------------------------------------------------------

    @Test
    void privacyCallsAreVerifiedLikeWebhooks() throws Exception {
        String secret = "whsec_privacy_secret";
        String body = "{\"id\":\"evt_9\",\"type\":\"privacy_request.completed\",\"data\":{\"id\":\"pr_1\"}}";
        long now = System.currentTimeMillis();
        Map<String, String> h = signed(secret, "evt_9", now, "unused", body, true, false, null);
        String tp = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";
        h.put("traceparent", tp);
        PrivacyCallVerifier verifier = new PrivacyCallVerifier(secret);
        PrivacyCall call = verifier.verify(body.getBytes(StandardCharsets.UTF_8), h);
        assertNotNull(call);
        assertEquals("evt_9", call.deliveryId());
        assertEquals(tp, call.traceparent());
        assertEquals("privacy_request.completed", call.event().get("type").asText());

        // a signed case request is not an event envelope
        String caseBody = "{\"caseId\":\"c1\",\"subjectId\":\"u1\"}";
        Map<String, String> h2 = signed(secret, "evt_10", now, "unused", caseBody, true, false, null);
        PrivacyCall plain = verifier.verify(caseBody.getBytes(StandardCharsets.UTF_8), h2);
        assertNotNull(plain);
        assertNull(plain.event());
        assertEquals("c1", plain.body().get("caseId").asText());

        // tampering is refused
        assertNull(verifier.verify((body + " ").getBytes(StandardCharsets.UTF_8), h));
        assertNull(new PrivacyCallVerifier("whsec_other").verify(body.getBytes(StandardCharsets.UTF_8), h));
    }

    // ---- operator login ------------------------------------------------------

    private static void reply(HttpExchange ex, int status, String body) throws IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("content-type", "application/json");
        ex.sendResponseHeaders(status, b.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(b);
        }
        ex.close();
    }

    @Test
    void operatorDeviceFlowExchangeAndLeakedKeys() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        List<String> forms = new ArrayList<>();
        AtomicInteger polls = new AtomicInteger();
        server.createContext("/v1/operator", ex -> reply(ex, 200, "{\"issuer\":\"" + base + "\",\"device_authorization_endpoint\":\"" + base + "/oauth2/device_authorization\","
                + "\"token_endpoint\":\"" + base + "/oauth2/token\",\"client_id\":\"atlas-cli\",\"scope\":\"openid operator\",\"token_exchange_endpoint\":\"" + base + "/v1/oauth/token\","
                + "\"token_exchange_audience\":\"atlas\",\"root_action_max_mfa_age_seconds\":300}"));
        server.createContext("/oauth2/device_authorization", ex -> {
            forms.add(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            reply(ex, 200, "{\"device_code\":\"dc_1\",\"user_code\":\"ABCD-EFGH\",\"verification_uri\":\"" + base + "/device\",\"verification_uri_complete\":\"" + base + "/device?c=ABCD-EFGH\",\"expires_in\":600,\"interval\":2}");
        });
        server.createContext("/oauth2/token", ex -> {
            forms.add(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            int n = polls.incrementAndGet();
            if (n == 1) {
                reply(ex, 400, "{\"error\":\"authorization_pending\"}");
            } else if (n == 2) {
                reply(ex, 400, "{\"error\":\"slow_down\"}");
            } else {
                reply(ex, 200, "{\"access_token\":\"operator_tok\",\"token_type\":\"Bearer\",\"expires_in\":900}");
            }
        });
        server.createContext("/v1/oauth/token", ex -> {
            String form = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            forms.add(form);
            if (form.contains("bad_token")) {
                reply(ex, 403, "{\"errors\":[{\"code\":\"STEP_UP_REQUIRED\",\"message\":\"Sign in with a second factor.\"}]}");
            } else {
                reply(ex, 200, "{\"access_token\":\"atlas_live_at_xyz\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"issued_token_type\":\"urn:ietf:params:oauth:token-type:access_token\",\"resource\":\"ins_1\"}");
            }
        });
        server.createContext("/v1/keys/revoke_leaked", ex -> {
            forms.add(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            reply(ex, 200, "{\"results\":[{\"index\":0,\"kind\":\"secret_key\",\"outcome\":\"revoked\"},{\"index\":1,\"kind\":\"unknown\",\"outcome\":\"not_found\"}]}");
        });
        server.start();
        try {
            List<Long> sleeps = new ArrayList<>();
            AtomicLong clock = new AtomicLong(1_000L);
            OperatorLogin login = new OperatorLogin(base, java.net.http.HttpClient.newHttpClient(), ms -> {
                sleeps.add(ms);
                clock.addAndGet(ms);
            }, clock::get);

            OperatorLogin.DeviceAuthorization start = login.start();
            assertEquals("ABCD-EFGH", start.userCode());
            assertEquals(base + "/device?c=ABCD-EFGH", start.verificationUriComplete());
            assertEquals("atlas-cli", start.config().clientId());
            assertEquals(300, start.config().rootActionMaxMfaAgeSeconds());
            assertTrue(forms.get(0).contains("client_id=atlas-cli"));
            assertTrue(forms.get(0).contains("scope=openid+operator"));

            OperatorLogin.TokenResponse operator = login.poll(start);
            assertEquals("operator_tok", operator.accessToken());
            assertEquals(List.of(2000L, 2000L, 7000L), sleeps);   // interval, interval, then +5 s after slow_down
            assertTrue(forms.get(1).contains("device_code=dc_1"));
            assertTrue(forms.get(1).contains(URLEncoder.encode("urn:ietf:params:oauth:grant-type:device_code", StandardCharsets.UTF_8)));

            OperatorLogin.TokenResponse scoped = login.exchange("operator_tok", "ins_1", List.of("users:read", "users:write"));
            assertEquals("atlas_live_at_xyz", scoped.accessToken());
            assertEquals("ins_1", scoped.resource());
            assertTrue(KeyFormat.isAccessToken(scoped.accessToken()));
            String exchange = forms.get(forms.size() - 1);
            assertTrue(exchange.contains("subject_token=operator_tok"));
            assertTrue(exchange.contains("resource=ins_1"));
            assertTrue(exchange.contains("scope=users%3Aread+users%3Awrite"));
            assertTrue(exchange.contains("audience=atlas"));

            OperatorLogin.OAuthFlowException stepUp = assertThrows(OperatorLogin.OAuthFlowException.class, () -> login.exchange("bad_token", "app_1", List.of("apps:write")));
            assertEquals("STEP_UP_REQUIRED", stepUp.code());
            assertEquals(403, stepUp.status());

            OperatorLogin.TokenResponse partner = login.partnerLinkToken("cid", "sec", "privacy:execute");
            assertEquals("atlas_live_at_xyz", partner.accessToken());
            assertTrue(forms.get(forms.size() - 1).contains("grant_type=client_credentials"));

            List<OperatorLogin.LeakedKeyResult> leaked = login.revokeLeaked(List.of("atlas_live_sk_a", "nope"), "https://github.com/x/y");
            assertEquals(2, leaked.size());
            assertEquals("revoked", leaked.get(0).outcome());
            assertEquals("not_found", leaked.get(1).outcome());
            assertTrue(forms.get(forms.size() - 1).contains("\"url\":\"https://github.com/x/y\""));

            AtlasClient viaToken = OperatorLogin.clientFromAccessToken("atlas_live_at_xyz", base);
            assertNotNull(viaToken.platform());
            assertThrows(IllegalArgumentException.class, () -> OperatorLogin.clientFromAccessToken("", base));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void operatorPollGivesUpWhenTheCodeExpires() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        server.createContext("/oauth2/token", ex -> {
            ex.getRequestBody().readAllBytes();
            reply(ex, 400, "{\"error\":\"authorization_pending\"}");
        });
        server.start();
        try {
            AtomicLong clock = new AtomicLong(0);
            OperatorLogin login = new OperatorLogin(base, java.net.http.HttpClient.newHttpClient(), ms -> clock.addAndGet(ms), clock::get);
            OperatorLogin.OperatorConfiguration cfg = new OperatorLogin.OperatorConfiguration(base, base + "/d", base + "/oauth2/token", "atlas-cli", "openid", base + "/v1/oauth/token", "atlas", 300);
            OperatorLogin.DeviceAuthorization dev = new OperatorLogin.DeviceAuthorization("dc", "UC", base + "/device", base + "/device?c=UC", 6, 2, cfg);
            OperatorLogin.OAuthFlowException e = assertThrows(OperatorLogin.OAuthFlowException.class, () -> login.poll(dev));
            assertEquals("expired_token", e.code());
        } finally {
            server.stop(0);
        }
    }

    // ---- back-channel logout handler ----------------------------------------

    private static final String ISSUER = "https://atlas.example.com";
    private static final String CLIENT = "atlas_client_1";
    private static final String KID = "bcl-key";
    private static final long NOW_MS = 1_800_000_000_000L;

    private static String logoutToken(RSAKey key, String jti) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER).subject("usr_1").audience(CLIENT)
                .issueTime(new Date(NOW_MS - 1_000)).expirationTime(new Date(NOW_MS + 120_000))
                .jwtID(jti).claim("sid", "sess_1")
                .claim("events", Map.of(LogoutTokenVerifier.BACKCHANNEL_LOGOUT_EVENT, Map.of())).build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KID).type(new JOSEObjectType("logout+jwt")).build(), claims);
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    private static String form(String token) {
        return "logout_token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
    }

    @Test
    void backchannelLogoutHandlerAnswersAsAtlasExpects() throws Exception {
        RSAKey key = new RSAKeyGenerator(2048).keyID(KID).generate();
        RSAKey other = new RSAKeyGenerator(2048).keyID(KID).generate();
        String jwks = new JWKSet(key.toPublicJWK()).toString();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/jwks", ex -> reply(ex, 200, jwks));
        server.start();
        try {
            LogoutTokenVerifier verifier = LogoutTokenVerifier.builder()
                    .jwksUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/v1/jwks")
                    .issuer(ISSUER).clientId(CLIENT).now(() -> NOW_MS).build();
            List<String> ended = new ArrayList<>();
            AtomicInteger failures = new AtomicInteger();
            BackchannelLogoutHandler handler = BackchannelLogoutHandler.builder()
                    .verifier(verifier)
                    .onLogout(claims -> {
                        if (failures.get() > 0) {
                            failures.decrementAndGet();
                            throw new IllegalStateException("store down");
                        }
                        ended.add(claims.sid());
                    })
                    .build();

            BackchannelLogoutHandler.Response get = handler.handle("GET", "");
            assertEquals(405, get.status());
            assertEquals("POST", get.headers().get("Allow"));
            assertEquals("no-store", get.headers().get("Cache-Control"));
            assertEquals(400, handler.handle("POST", "").status());
            assertEquals(400, handler.handle("POST", form("not-a-jwt")).status());
            assertEquals(400, handler.handle("POST", form(logoutToken(other, "jti_bad"))).status());

            BackchannelLogoutHandler.Response ok = handler.handle("POST", form(logoutToken(key, "jti_1")));
            assertEquals(200, ok.status());
            assertEquals("no-store", ok.headers().get("Cache-Control"));
            assertEquals(List.of("sess_1"), ended);

            // the same token again: acknowledged, not handled twice
            BackchannelLogoutHandler.Response replay = handler.handle("POST", form(logoutToken(key, "jti_1")));
            assertEquals(200, replay.status());
            assertTrue(replay.body().contains("replayed"));
            assertEquals(1, ended.size());

            // your side failing: 503 + Retry-After, and the token is not remembered, so the retry is handled
            failures.set(1);
            BackchannelLogoutHandler.Response down = handler.handle("POST", form(logoutToken(key, "jti_2")));
            assertEquals(503, down.status());
            assertEquals("30", down.headers().get("Retry-After"));
            assertEquals(200, handler.handle("POST", form(logoutToken(key, "jti_2"))).status());
            assertEquals(2, ended.size());
        } finally {
            server.stop(0);
        }

        // keys unreachable: 503 so Atlas retries
        LogoutTokenVerifier offline = LogoutTokenVerifier.builder().jwksUrl("http://127.0.0.1:1/v1/jwks")
                .issuer(ISSUER).clientId(CLIENT).now(() -> NOW_MS).build();
        BackchannelLogoutHandler h = BackchannelLogoutHandler.builder().verifier(offline).onLogout(c -> { }).retryAfterSeconds(7).build();
        BackchannelLogoutHandler.Response r = h.handle("POST", form(logoutToken(key(), "jti_3")));
        assertEquals(503, r.status());
        assertEquals("7", r.headers().get("Retry-After"));
    }

    private static RSAKey cached;

    private static RSAKey key() throws Exception {
        if (cached == null) {
            cached = new RSAKeyGenerator(2048).keyID(KID).generate();
        }
        return cached;
    }
}
