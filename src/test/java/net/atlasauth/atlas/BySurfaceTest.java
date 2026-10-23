package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import net.atlasauth.atlas.models.ActionTemplateBody;
import net.atlasauth.atlas.models.BillingFeatureCheck;
import net.atlasauth.atlas.models.BillingPlan;
import net.atlasauth.atlas.models.BillingSettings;
import net.atlasauth.atlas.models.BillingSubscription;
import net.atlasauth.atlas.models.CustomOAuthProvider;
import net.atlasauth.atlas.models.CustomOAuthProviderBody;
import net.atlasauth.atlas.models.DormantAccountsSettings;
import net.atlasauth.atlas.models.OAuthClient;
import net.atlasauth.atlas.models.OAuthTokenLifetimes;
import net.atlasauth.atlas.models.RateLimitPolicy;
import net.atlasauth.atlas.models.RateLimitRule;
import net.atlasauth.atlas.models.ReportBillingUsageBody;
import net.atlasauth.atlas.models.SamlEncryptionKey;
import net.atlasauth.atlas.models.Session;
import net.atlasauth.atlas.models.SigningKeyProviderSettings;
import net.atlasauth.atlas.models.UpdateAttackProtectionBody;
import net.atlasauth.atlas.models.UpdateBillingPlanBody;
import net.atlasauth.atlas.models.UpdateOAuthClientBody;
import net.atlasauth.atlas.models.UpdateRateLimitPolicyBody;
import net.atlasauth.atlas.models.UpdateResourceServerBody;
import net.atlasauth.atlas.models.UpdateSsoConnectionBody;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import net.atlasauth.atlas.verify.Introspector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Billing add-ons / settings / checks, custom OAuth providers, the Actions gallery, session
 * custom data, sign-in settings, SAML encryption keys, dormant accounts, token formats /
 * lifetimes, KMS signing keys, opaque-token introspection, device approval and BotID.
 */
class BySurfaceTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String ISSUER = "https://issuer.test";
    private static final String AUD = "https://api.test";

    private MockBackend mock;
    private AtlasClient atlas;
    private HttpServer server;
    private String base;
    private final List<String> forms = new ArrayList<>();
    private final List<Map<String, String>> headers = new ArrayList<>();
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicInteger introspections = new AtomicInteger();
    private final AtomicInteger deviceStatus = new AtomicInteger(200);
    private ECKey ec;

    @BeforeEach
    void setUp() throws Exception {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
        ec = new ECKeyGenerator(Curve.P_256).keyID("ec-1").algorithm(JWSAlgorithm.ES256).generate();
        String jwks = new JWKSet(List.of(ec.toPublicJWK())).toString();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/jwks", ex -> send(ex, 200, jwks));
        server.createContext("/oauth2/introspect", ex -> {
            introspections.incrementAndGet();
            String form = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            forms.add(form);
            headers.add(Map.of("authorization", ex.getRequestHeaders().getFirst("Authorization")));
            String token = "";
            for (String pair : form.split("&")) {
                if (pair.startsWith("token=")) {
                    token = URLDecoder.decode(pair.substring(6), StandardCharsets.UTF_8);
                }
            }
            if (token.equals("oat_down")) {
                send(ex, 500, "");
                return;
            }
            if (!active.get() || token.equals("oat_dead")) {
                send(ex, 200, "{\"active\":false}");
                return;
            }
            String iss = token.equals("oat_foreign") ? "https://other.test" : ISSUER;
            send(ex, 200, "{\"active\":true,\"iss\":\"" + iss + "\",\"sub\":\"u_1\",\"aud\":\"" + AUD
                    + "\",\"scope\":\"read\",\"client_id\":\"oc_1\",\"exp\":" + (System.currentTimeMillis() / 1000 + 3600) + "}");
        });
        server.createContext("/v1/client/me/device_authorizations", ex -> {
            String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            forms.add(ex.getRequestURI().getPath() + " " + body);
            headers.add(Map.of("authorization", ex.getRequestHeaders().getFirst("Authorization"),
                    "pk", String.valueOf(ex.getRequestHeaders().getFirst("x-publishable-key"))));
            if (deviceStatus.get() != 200) {
                send(ex, 403, "{\"errors\":[{\"code\":\"STEP_UP_REQUIRED\",\"message\":\"Re-authenticate.\"}]}");
                return;
            }
            send(ex, 200, "{\"object\":\"device_authorization\",\"id\":\"da_1\",\"user_code\":\"WDJB-MJHT\",\"client\":{\"name\":\"TV\"},\"requires_step_up\":true}");
        });
        server.start();
        base = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    private static void send(com.sun.net.httpserver.HttpExchange ex, int status, String body) throws java.io.IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, b.length == 0 ? -1 : b.length);
        if (b.length > 0) {
            try (OutputStream os = ex.getResponseBody()) {
                os.write(b);
            }
        }
        ex.close();
    }

    @AfterEach
    void tearDown() {
        mock.close();
        server.stop(0);
    }

    private JsonNode body() throws Exception {
        return JSON.readTree(mock.requestBody);
    }

    @Test
    void billing() throws Exception {
        mock.reply(200, "{\"object\":\"billing_subscription\",\"id\":\"bsub_1\",\"entitlements\":{\"features\":[\"sso\"],\"limits\":{\"projects\":10}}}");
        BillingSubscription sub = atlas.billing().getSubscription("bsub_1");
        assertEquals("GET", mock.method);
        assertEquals("/v1/billing/subscriptions/bsub_1", mock.path);
        assertEquals(List.of("sso"), sub.entitlements().features());
        assertEquals(10.0, sub.entitlements().limits().get("projects"));
        mock.reply(200, "{\"object\":\"billing_plan\",\"id\":\"bp_1\",\"kind\":\"addon\",\"seat_feature_id\":\"seats\",\"limits\":{\"projects\":-1}}");
        BillingPlan plan = atlas.billing().syncPlanEntitlements("bp_1");
        assertEquals("/v1/billing/plans/bp_1/sync_entitlements", mock.path);
        assertEquals("addon", plan.kind());
        assertEquals("seats", plan.seatFeatureId());
        mock.reply(200, "{\"object\":\"billing_settings\",\"default_limit_merge\":\"sum\",\"limit_merge\":{\"seats\":\"max\"}}");
        BillingSettings set = atlas.billing().updateSettings("sum", Map.of("seats", "max"));
        assertEquals("PUT", mock.method);
        assertEquals("/v1/billing/settings", mock.path);
        assertEquals("max", body().get("limit_merge").get("seats").asText());
        assertEquals("sum", set.defaultLimitMerge());
        atlas.billing().getSettings();
        assertEquals("GET", mock.method);
        mock.reply(200, "{\"object\":\"billing_feature_check\",\"feature\":\"api\",\"allowed\":true,\"source\":\"provider\",\"remaining\":5}");
        BillingFeatureCheck chk = atlas.billing().checkFeature("org", "org_1", "api", 2.0);
        assertEquals("/v1/billing/check", mock.path);
        assertEquals(2.0, body().get("required").asDouble());
        assertTrue(chk.allowed());
        assertEquals(5.0, chk.remaining());
        mock.reply(200, "{}");
        atlas.billing().reportUsage(new ReportBillingUsageBody().subjectType("user").subjectId("u_1").quantity(1.0)
                .idempotencyKey("k").feature("api").subscriptionId("bsub_2"));
        assertEquals("bsub_2", body().get("subscription_id").asText());
        atlas.billing().updatePlan("bp_1", new UpdateBillingPlanBody().kind("addon").set("seat_feature_id", null));
        assertTrue(body().has("seat_feature_id") && body().get("seat_feature_id").isNull());
        assertEquals("addon", body().get("kind").asText());
    }

    @Test
    void customProvidersAndSso() throws Exception {
        mock.reply(200, "{\"object\":\"custom_oauth_provider\",\"id\":\"coap_1\",\"provider_key\":\"custom_acme\",\"client_jwks_uri\":\"https://fapi/jwks\",\"has_secret\":true,\"profile_mapping\":{\"fields\":{\"email\":\"mail\"}},\"signing_keys\":[{\"kid\":\"k1\",\"alg\":\"ES256\",\"status\":\"active\"}]}");
        CustomOAuthProvider p = atlas.customOAuthProviders().create(new CustomOAuthProviderBody().providerKey("custom_acme")
                .displayName("Acme").issuer("https://idp.test").tokenEndpointAuthMethod("private_key_jwt")
                .profileMapping(Map.of("fields", Map.of("email", "mail"))));
        assertEquals("POST", mock.method);
        assertEquals("/v1/custom_oauth_providers", mock.path);
        assertEquals("mail", body().get("profile_mapping").get("fields").get("email").asText());
        assertEquals("private_key_jwt", body().get("token_endpoint_auth_method").asText());
        assertEquals("https://fapi/jwks", p.clientJwksUri());
        assertTrue(p.hasSecret());
        assertEquals("k1", p.signingKeys().get(0).kid());
        assertEquals("mail", p.profileMapping().get("fields").get("email").asText());
        atlas.customOAuthProviders().update("coap_1", new CustomOAuthProviderBody().set("icon_url", null));
        assertEquals("PATCH", mock.method);
        assertTrue(body().get("icon_url").isNull());
        atlas.customOAuthProviders().discover("https://idp.test", null);
        assertEquals("/v1/custom_oauth_providers/discover", mock.path);
        assertFalse(body().has("discovery_url"));
        atlas.customOAuthProviders().previewProfileMapping("coap_1", Map.of("id_token", "a.b.c"));
        assertEquals("/v1/custom_oauth_providers/coap_1/profile_mapping/preview", mock.path);
        atlas.customOAuthProviders().previewProfileMapping(Map.of("userinfo", Map.of()));
        assertEquals("/v1/custom_oauth_providers/profile_mapping/preview", mock.path);
        atlas.customOAuthProviders().rotateSigningKey("coap_1", "PS256");
        assertEquals("PS256", body().get("alg").asText());
        atlas.customOAuthProviders().retireSigningKey("coap_1", "k1");
        assertEquals("/v1/custom_oauth_providers/coap_1/signing_keys/k1/retire", mock.path);
        atlas.ssoConnections().rotateSigningKey("sso_1", null);
        assertEquals("/v1/sso_connections/sso_1/signing_keys", mock.path);
        assertEquals(0, body().size());
        atlas.ssoConnections().update("sso_1", new UpdateSsoConnectionBody().samlSingleLogout(true)
                .samlIdpSloSoapUrl("https://idp.test/soap").samlRequireEncryptedAssertions(true)
                .set("saml_idp_slo_response_url", null));
        assertEquals("https://idp.test/soap", body().get("saml_idp_slo_soap_url").asText());
        assertTrue(body().get("saml_require_encrypted_assertions").asBoolean());
        assertTrue(body().get("saml_idp_slo_response_url").isNull());
        mock.reply(200, "{\"object\":\"sso_saml_encryption_key\",\"connection_id\":\"sso_1\",\"certificate\":\"PEM\",\"require_encrypted_assertions\":true}");
        SamlEncryptionKey k = atlas.ssoConnections().createSamlEncryptionKey("sso_1");
        assertEquals("POST", mock.method);
        assertEquals("/v1/sso_connections/sso_1/saml_encryption_key", mock.path);
        assertEquals("PEM", k.certificate());
        assertTrue(k.requireEncryptedAssertions());
        atlas.ssoConnections().retirePreviousSamlEncryptionKey("sso_1");
        assertEquals("DELETE", mock.method);
        assertEquals("/v1/sso_connections/sso_1/saml_encryption_key/previous", mock.path);
    }

    @Test
    void actionsGallery() throws Exception {
        mock.reply(200, "{\"object\":\"list\",\"data\":[{\"slug\":\"geo-fence\",\"uses_http\":true}]}");
        var list = atlas.actionGallery().list("geo", null, "post_login", null);
        assertEquals("/v1/action_gallery", mock.path);
        assertEquals("q=geo&trigger=post_login", mock.rawQuery);
        assertTrue(list.data().get(0).usesHttp());
        mock.reply(200, "{}");
        atlas.actionGallery().install("geo-fence", null, null, Map.of("K", "v"));
        assertEquals("/v1/action_gallery/geo-fence/install", mock.path);
        assertEquals("v", body().get("secrets").get("K").asText());
        atlas.actionGallery().update("act_1", null);
        assertEquals("/v1/actions/act_1/gallery_update", mock.path);
        atlas.actionTemplates().create(new ActionTemplateBody().slug("mine").name("Mine").authorName("Me")
                .trigger("post_login").version("1.0.0").code("x").submit(true));
        assertEquals("/v1/action_templates", mock.path);
        assertEquals("Me", body().get("author_name").asText());
        assertTrue(body().get("submit").asBoolean());
        atlas.actionTemplates().submitVersion("atpl_1", "1.0.0");
        assertEquals("/v1/action_templates/atpl_1/versions/1.0.0/submit", mock.path);
        atlas.actionTemplates().unlist("atpl_1");
        assertEquals("/v1/action_templates/atpl_1/unlist", mock.path);
    }

    @Test
    void signInEnterpriseAndOp() throws Exception {
        mock.reply(200, "{\"object\":\"session\",\"id\":\"sess_1\",\"custom_data\":{\"plan\":\"pro\"}}");
        Map<String, Object> patch = new HashMap<>();
        patch.put("plan", null);
        patch.put("seat", 2);
        Session s = atlas.sessions().updateCustomData("sess_1", patch);
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/sessions/sess_1/custom_data", mock.path);
        assertTrue(body().get("custom_data").get("plan").isNull());
        assertEquals("pro", s.customData().get("plan"));
        atlas.sessions().setCustomData("sess_1", null);
        assertEquals("PUT", mock.method);
        assertTrue(body().get("custom_data").isNull());

        mock.reply(200, "{}");
        atlas.instanceSecurity().setWalletRpcSecret(1, "https://rpc.test");
        assertEquals("/v1/instance/wallet_rpc_secret", mock.path);
        assertEquals(1, body().get("chain_id").asInt());
        mock.reply(200, "{\"object\":\"rate_limit_policy\",\"rules\":[{\"group\":\"fapi.sign_in\",\"window_seconds\":60,\"max\":5,\"key\":\"ip\"}]}");
        RateLimitPolicy pol = atlas.rateLimitPolicy().update(new UpdateRateLimitPolicyBody()
                .rules(List.of(new RateLimitRule("fapi.sign_in", 60, 5, "ip"))));
        assertEquals(60, body().get("rules").get(0).get("window_seconds").asInt());
        assertEquals("ip", pol.rules().get(0).key);
        mock.reply(200, "{}");
        atlas.instance().setAccountLinkingTrust(Map.of("google", "always"));
        assertEquals("always", body().get("auth_config").get("accountLinkingTrust").get("google").asText());
        atlas.instance().setCaptchaConfig(Map.of("provider", "vercel_botid", "attestationUrl", "/api/botid"));
        assertEquals("/api/botid", body().get("auth_config").get("captcha").get("attestationUrl").asText());
        atlas.attackProtection().update(new UpdateAttackProtectionBody()
                .dormantAccounts(new DormantAccountsSettings().enabled(true).thresholdDays(120)));
        assertEquals(120, body().get("dormant_accounts").get("threshold_days").asInt());

        mock.reply(200, "{\"object\":\"oauth_client\",\"access_token_format\":\"opaque\",\"token_lifetimes\":{\"access_token_ttl_seconds\":600}}");
        OAuthClient oc = atlas.oauthClients().update("oc_1", new UpdateOAuthClientBody()
                .tokenLifetimes(new OAuthTokenLifetimes().accessTokenTtlSeconds(600L)).set("access_token_format", null));
        assertEquals(600, body().get("token_lifetimes").get("access_token_ttl_seconds").asInt());
        assertTrue(body().get("access_token_format").isNull());
        assertEquals("opaque", oc.accessTokenFormat());
        assertEquals(600L, oc.tokenLifetimes().accessTokenTtlSeconds);
        mock.reply(200, "{}");
        atlas.resourceServers().update("rs_1", new UpdateResourceServerBody().accessTokenFormat("opaque")
                .introspectionClientIds(List.of("oc_api")));
        assertEquals("oc_api", body().get("introspection_client_ids").get(0).asText());
        mock.reply(200, "{\"object\":\"signing_key_provider\",\"provider\":\"aws_kms\",\"active\":true,\"key_version\":\"arn\",\"rotation\":{\"kid\":\"k2\"}}");
        SigningKeyProviderSettings kp = atlas.instanceSecurity().setSigningKeyProvider("aws_kms",
                Map.of("region", "us-east-1"), Map.of("access_key_id", "a"), null, null);
        assertEquals("PUT", mock.method);
        assertEquals("/v1/instance/signing_key_provider", mock.path);
        assertEquals("a", body().get("credentials").get("access_key_id").asText());
        assertEquals("k2", kp.rotation().get("kid").asText());
        atlas.instanceSecurity().removeSigningKeyProvider(null, true);
        assertEquals("DELETE", mock.method);
        assertTrue(body().get("emergency").asBoolean());
        assertTrue(AtlasConstants.CAPTCHA_PROVIDERS.contains("captchafox"));
        assertEquals("user.reactivated", AtlasConstants.WEBHOOK_EVENT_USER_REACTIVATED);
    }

    @Test
    void botIdAttestation() throws Exception {
        String secret = "k".repeat(32);
        String tok = BotId.createAttestation(secret, false, false, 3600, 1_800_000_000L);
        assertTrue(tok.startsWith(BotId.PREFIX));
        String[] rest = tok.substring(BotId.PREFIX.length()).split("\\.");
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String expect = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal((BotId.PREFIX + rest[0]).getBytes(StandardCharsets.UTF_8)));
        assertEquals(expect, rest[1]);
        JsonNode p = JSON.readTree(Base64.getUrlDecoder().decode(rest[0]));
        assertEquals(1, p.get("v").asInt());
        assertFalse(p.get("is_bot").asBoolean());
        assertEquals(300, p.get("exp").asLong() - p.get("iat").asLong());
        assertTrue(p.get("jti").asText().length() >= 20);
        assertThrows(IllegalArgumentException.class, () -> BotId.createAttestation("short", false, false));
    }

    @Test
    void deviceAuthorizations() {
        DeviceAuthorizations d = new DeviceAuthorizations(base + "/", "sess_jwt", "pk_x", null);
        Map<String, Object> auth = d.lookup(" wdjb mjht ");
        assertTrue(forms.get(forms.size() - 1).startsWith("/v1/client/me/device_authorizations/lookup"));
        assertTrue(forms.get(forms.size() - 1).contains("\"WDJB-MJHT\""));
        assertEquals("Bearer sess_jwt", headers.get(headers.size() - 1).get("authorization"));
        assertEquals("pk_x", headers.get(headers.size() - 1).get("pk"));
        assertEquals("TV", ((Map<?, ?>) auth.get("client")).get("name"));
        d.approve(auth);
        assertTrue(forms.get(forms.size() - 1).startsWith("/v1/client/me/device_authorizations/da_1/approve"));
        d.deny(auth);
        assertTrue(forms.get(forms.size() - 1).startsWith("/v1/client/me/device_authorizations/da_1/deny"));
        deviceStatus.set(403);
        DeviceAuthorizations.DeviceAuthorizationException e = assertThrows(
                DeviceAuthorizations.DeviceAuthorizationException.class, () -> d.approve(auth));
        assertTrue(e.needsReverification());
        assertFalse(e.isInvalidCode());
        assertEquals("abc", DeviceAuthorizations.formatUserCode("abc"));
        assertEquals("WDJB-MJHT", DeviceAuthorizations.userCodeFromUrl("https://x.test/d?user_code=wdjbmjht"));
        assertNull(DeviceAuthorizations.userCodeFromUrl("nope"));
    }

    @Test
    void opaqueTokensViaIntrospection() throws Exception {
        AccessTokenVerifier v = AccessTokenVerifier.builder().issuer(ISSUER).audience(AUD).jwksUrl(base + "/jwks")
                .introspection(Introspector.builder().issuer(base).clientId("oc_api").clientSecret("s3cret").build())
                .build();
        AccessTokenVerifier.Result ok = v.verify("Bearer oat_live", "read");
        assertTrue(ok.ok(), ok.description());
        assertEquals("u_1", ok.subject());
        assertEquals("oc_1", ok.clientId());
        assertEquals("Basic " + Base64.getEncoder().encodeToString("oc_api:s3cret".getBytes(StandardCharsets.UTF_8)),
                headers.get(0).get("authorization"));
        assertTrue(forms.get(0).contains("token_type_hint=access_token"));
        v.verify("Bearer oat_live");
        assertEquals(1, introspections.get(), "cached");
        assertEquals(403, v.verify("Bearer oat_live", "admin").status());
        assertFalse(v.verify("Bearer oat_dead").ok());
        int n = introspections.get();
        v.verify("Bearer oat_dead");
        assertEquals(n, introspections.get(), "negative answer cached");
        AccessTokenVerifier.Result foreign = v.verify("Bearer oat_foreign");
        assertFalse(foreign.ok());
        assertTrue(foreign.description().contains("another authorization server"));
        AccessTokenVerifier.Result down = v.verify("Bearer oat_down");
        assertFalse(down.ok());
        assertTrue(down.description().contains("could not be validated"));

        AccessTokenVerifier plain = AccessTokenVerifier.builder().issuer(ISSUER).audience(AUD).jwksUrl(base + "/jwks").build();
        assertTrue(plain.verify("Bearer oat_live").description().contains("introspection"));

        AccessTokenVerifier live = AccessTokenVerifier.builder().issuer(ISSUER).audience(AUD).jwksUrl(base + "/jwks")
                .introspection(Introspector.builder().issuer(base).clientId("oc_api").clientSecret("s3cret")
                        .introspectJwts(true).build())
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256).keyID("ec-1").build(),
                new JWTClaimsSet.Builder().issuer(ISSUER).subject("u_2").audience(AUD)
                        .expirationTime(new Date(System.currentTimeMillis() + 60_000)).claim("token_use", "access_token").build());
        jwt.sign(new ECDSASigner(ec));
        assertTrue(plain.verify("Bearer " + jwt.serialize()).ok());
        active.set(false);
        assertFalse(live.verify("Bearer " + jwt.serialize()).ok());
        assertThrows(IllegalArgumentException.class, () -> Introspector.builder().issuer(ISSUER).build());
    }

    @Test
    void introspectorCacheBoundsAndExpiry() throws Exception {
        AtomicLong clock = new AtomicLong(1_800_000_000_000L);
        HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicInteger hits = new AtomicInteger();
        s.createContext("/i", ex -> {
            hits.incrementAndGet();
            ex.getRequestBody().readAllBytes();
            send(ex, 200, "{\"active\":true,\"exp\":1800000005}");
        });
        s.start();
        try {
            Introspector i = Introspector.builder().endpoint("http://127.0.0.1:" + s.getAddress().getPort() + "/i")
                    .clientId("a").clientSecret("b").cacheMax(2).now(clock::get).build();
            for (String t : List.of("t1", "t2", "t3")) {
                assertNotNull(i.introspect(t));
            }
            assertEquals(2, i.cacheSize());
            i.introspect("t3");
            int n = hits.get();
            clock.addAndGet(6000);
            i.introspect("t3");
            assertEquals(n + 1, hits.get(), "never reused past exp");
        } finally {
            s.stop(0);
        }
    }
}
