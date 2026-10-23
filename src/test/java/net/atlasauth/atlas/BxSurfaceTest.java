package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import net.atlasauth.atlas.models.ApiKeyRateLimit;
import net.atlasauth.atlas.models.ApiKeyVerification;
import net.atlasauth.atlas.models.BillingSubscription;
import net.atlasauth.atlas.models.CreateApiKeyBody;
import net.atlasauth.atlas.models.UpdateOAuthClientBody;
import net.atlasauth.atlas.models.UserSearchParams;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import net.atlasauth.atlas.verify.ProtectCondition;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.SigningAlgs;
import net.atlasauth.atlas.verify.VerifyResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * App roles, org teams/roles, user search + timed bans, API key controls, billing providers and
 * subscription management, OAuth server administration, ES256 verification, app-claim helpers
 * and the access-token verifier.
 */
class BxSurfaceTest {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String ISSUER = "https://issuer.test";
    private static final String AUD = "https://api.test";

    private MockBackend mock;
    private AtlasClient atlas;
    private HttpServer jwksServer;
    private ECKey ec;
    private RSAKey rsa;
    private String jwksUrl;

    @BeforeEach
    void setUp() throws Exception {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
        ec = new ECKeyGenerator(Curve.P_256).keyID("ec-1").algorithm(JWSAlgorithm.ES256).generate();
        rsa = new RSAKeyGenerator(2048).keyID("rs-1").algorithm(JWSAlgorithm.RS256).generate();
        String jwks = new JWKSet(List.of(ec.toPublicJWK(), rsa.toPublicJWK())).toString();
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
        mock.close();
        jwksServer.stop(0);
    }

    private JsonNode body() throws Exception {
        return JSON.readTree(mock.requestBody);
    }

    @Test
    void appRolesAndOrgTeamsRoles() throws Exception {
        atlas.appRoles().create("support", "Support", null, List.of("tickets:read"));
        assertEquals("POST", mock.method);
        assertEquals("/v1/app_roles", mock.path);
        assertEquals("support", body().get("key").asText());
        atlas.appRoles().listUsers("ar_1", 5, 10);
        assertEquals("/v1/app_roles/ar_1/users", mock.path);
        assertEquals("limit=5&offset=10", mock.rawQuery);
        atlas.appRoles().changeForUser("u_1", List.of("support"), null);
        assertEquals("/v1/users/u_1/app_roles", mock.path);
        assertFalse(body().has("remove"));
        atlas.appRoles().removeFromUser("u_1", "support");
        assertEquals("DELETE", mock.method);
        assertEquals("/v1/users/u_1/app_roles/support", mock.path);

        atlas.organizations().teams().list("org_1", "u_1");
        assertEquals("/v1/organizations/org_1/teams", mock.path);
        assertEquals("user_id=u_1", mock.rawQuery);
        atlas.organizations().teams().addMember("org_1", "tm_1", "u_2");
        assertEquals("/v1/organizations/org_1/teams/tm_1/members", mock.path);
        assertEquals("u_2", body().get("user_id").asText());
        atlas.organizations().roles().list("org_1", true);
        assertEquals("own=true", mock.rawQuery);
        atlas.organizations().roles().delete("org_1", "r_1", "member");
        assertEquals("DELETE", mock.method);
        assertEquals("reassign_to=member", mock.rawQuery);
        atlas.organizations().checkSlug("acme");
        assertEquals("/v1/organizations/check_slug", mock.path);
        assertEquals("slug=acme", mock.rawQuery);
        atlas.organizations().invitations().resend("org_1", "inv_1");
        assertEquals("/v1/organizations/org_1/invitations/inv_1/resend", mock.path);
    }

    @Test
    void userSearchTimedBanAndApiKeyControls() throws Exception {
        atlas.users().list(new UserSearchParams().query("ada").banned(true).createdAfter(1000L).orderBy("-created_at"));
        assertEquals("/v1/users", mock.path);
        assertEquals("query=ada&banned=true&created_after=1000&order_by=-created_at", mock.rawQuery);
        atlas.users().ban("u_1", "spam", null, 3600L, null);
        assertEquals("/v1/users/u_1/ban", mock.path);
        assertEquals(3600, body().get("expires_in_seconds").asInt());
        assertFalse(body().has("note"));

        mock.reply(200, "{\"object\":\"api_key_verification\",\"valid\":false,\"code\":\"insufficient_scope\",\"missing_scopes\":[\"write\"]}");
        ApiKeyVerification v = atlas.apiKeys().verify("ak_x", List.of("read", "write"));
        assertEquals(2, body().get("required_scopes").size());
        assertFalse(v.valid());
        assertEquals("insufficient_scope", v.code());
        assertEquals(List.of("write"), v.missingScopes());
        mock.reply(200, "{}");
        atlas.apiKeys().create(new CreateApiKeyBody("user", "u_1").prefix("acme").enabled(false)
                .rateLimit(new ApiKeyRateLimit(10, 60)).remaining(5L).expiresInSeconds(86400L));
        JsonNode b = body();
        assertEquals("acme", b.get("prefix").asText());
        assertEquals(10, b.get("rate_limit").get("max").asInt());
        assertEquals(60, b.get("rate_limit").get("window_seconds").asInt());
        assertEquals(86400, b.get("expires_in_seconds").asInt());
    }

    @Test
    void billingProvidersAndSubscriptions() throws Exception {
        atlas.billing().setProvider("polar", "test", Map.of("access_token", "x"));
        assertEquals("PUT", mock.method);
        assertEquals("/v1/billing/providers/polar", mock.path);
        assertEquals("x", body().get("config").get("access_token").asText());
        atlas.billing().deleteProvider("polar", true);
        assertEquals("force=true", mock.rawQuery);
        mock.reply(200, "{\"object\":\"billing_subscription\",\"id\":\"sub_1\",\"cancel_at_period_end\":true,\"scheduled\":true,\"provider\":\"polar\"}");
        BillingSubscription sub = atlas.billing().changeSubscription("sub_1", "p_2", "year", true);
        assertEquals("/v1/billing/subscriptions/sub_1/change", mock.path);
        assertTrue(body().get("schedule_at_period_end").asBoolean());
        assertTrue(sub.scheduled());
        assertTrue(sub.cancelAtPeriodEnd());
        assertEquals("polar", sub.provider());
        atlas.billing().cancelSubscription("sub_1", true);
        assertTrue(body().get("immediately").asBoolean());
        atlas.billing().restoreSubscription("sub_1");
        assertEquals("/v1/billing/subscriptions/sub_1/restore", mock.path);
    }

    @Test
    void oauthServerAdministration() throws Exception {
        atlas.instanceSecurity().rotateSigningKey("ES256", null, null);
        assertEquals("/v1/instance/signing_keys/rotate", mock.path);
        assertEquals("ES256", body().get("alg").asText());
        assertFalse(body().has("emergency"));
        atlas.oauthInitialAccessTokens().create("ci", null, 3);
        assertEquals(3, body().get("max_uses").asInt());
        atlas.oauthInitialAccessTokens().revoke("iat_1");
        assertEquals("DELETE", mock.method);
        atlas.oauthClients().update("oc_1", new UpdateOAuthClientBody().disabled(true).requirePkce(true)
                .subjectType("pairwise").secretExpiresInSeconds(3600L));
        JsonNode b = body();
        assertTrue(b.get("disabled").asBoolean());
        assertTrue(b.get("require_pkce").asBoolean());
        assertEquals("pairwise", b.get("subject_type").asText());
        assertEquals(3600, b.get("secret_expires_in_seconds").asInt());
        atlas.userHooks().listBeforeUserCreateCalls(null, "failed", false, null);
        assertEquals("status=failed&test=false", mock.rawQuery);
        atlas.referrals().getCall("rc_1");
        assertEquals("/v1/instance/referrals/calls/rc_1", mock.path);
    }

    // ── Verification ──────────────────────────────────────────────────────

    private String sign(JWTClaimsSet claims, JWSAlgorithm alg, String kid) throws Exception {
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(alg).keyID(kid).build(), claims);
        if (alg == JWSAlgorithm.ES256) {
            jwt.sign(new ECDSASigner(ec));
        } else if (alg == JWSAlgorithm.RS256) {
            jwt.sign(new RSASSASigner(rsa));
        } else {
            jwt.sign(new MACSigner("k".repeat(64)));
        }
        return jwt.serialize();
    }

    private JWTClaimsSet.Builder session() {
        long now = System.currentTimeMillis();
        return new JWTClaimsSet.Builder().issuer(ISSUER).subject("user_1").expirationTime(new Date(now + 300_000));
    }

    @Test
    void sessionVerifierAcceptsEs256AndAppClaims() throws Exception {
        SessionVerifier v = SessionVerifier.builder().jwksUrl(jwksUrl).issuer(ISSUER).build();
        VerifyResult r = v.verify(sign(session().claim("app_roles", List.of("support"))
                .claim("app_permissions", List.of("tickets:read")).build(), JWSAlgorithm.ES256, "ec-1"));
        assertTrue(r.ok());
        assertTrue(r.claims().hasAppRole("support"));
        assertTrue(r.claims().hasAppPermission("tickets:read"));
        assertFalse(r.claims().hasAppPermission("tickets:write"));
        assertTrue(r.has(new ProtectCondition().appRole("support")));
        assertFalse(r.has(new ProtectCondition().appPermission("tickets:write")));
        assertTrue(v.verify(sign(session().build(), JWSAlgorithm.RS256, "rs-1")).ok());
        assertFalse(v.verify(sign(session().build(), JWSAlgorithm.ES256, "rs-1")).ok());
        assertFalse(v.verify(sign(session().build(), JWSAlgorithm.HS256, "ec-1")).ok());
        assertEquals(List.of(JWSAlgorithm.RS256, JWSAlgorithm.ES256), SigningAlgs.ALGS);
    }

    private JWTClaimsSet.Builder access() {
        return session().audience(AUD).claim("token_use", "access_token").claim("scope", "read write")
                .claim("client_id", "oc_1");
    }

    @Test
    void accessTokenVerifier() throws Exception {
        AccessTokenVerifier v = AccessTokenVerifier.builder().issuer(ISSUER + "/").audience(AUD)
                .requiredScopes("read").jwksUrl(jwksUrl)
                .resourceMetadataUrl(AUD + "/.well-known/oauth-protected-resource").build();
        AccessTokenVerifier.Result ok = v.verify("Bearer " + sign(access().build(), JWSAlgorithm.ES256, "ec-1"));
        assertTrue(ok.ok(), ok.description());
        assertEquals("user_1", ok.subject());
        assertEquals("oc_1", ok.clientId());
        assertEquals(List.of("read", "write"), ok.scopes());

        AccessTokenVerifier.Result none = v.verify(null);
        assertEquals(401, none.status());
        assertNull(none.error());
        assertEquals("Bearer scope=\"read\", resource_metadata=\"" + AUD + "/.well-known/oauth-protected-resource\"",
                none.wwwAuthenticate());
        assertEquals(400, v.verify("Basic abc").status());
        AccessTokenVerifier.Result wrong = v.verify("Bearer " + sign(access().audience("https://other.test").build(), JWSAlgorithm.ES256, "ec-1"));
        assertEquals(401, wrong.status());
        assertTrue(wrong.wwwAuthenticate().contains("error=\"invalid_token\""));
        assertFalse(v.verify("Bearer " + sign(access().claim("token_use", "session").build(), JWSAlgorithm.ES256, "ec-1")).ok());
        assertFalse(v.verify("Bearer " + sign(access().claim("cnf", Map.of("jkt", "x")).build(), JWSAlgorithm.ES256, "ec-1")).ok());
        assertFalse(v.verify("Bearer " + sign(access().expirationTime(new Date(System.currentTimeMillis() - 600_000)).build(), JWSAlgorithm.ES256, "ec-1")).ok());
        AccessTokenVerifier.Result shortScope = v.verify("Bearer " + sign(access().claim("scope", "write").build(), JWSAlgorithm.RS256, "rs-1"), "admin");
        assertEquals(403, shortScope.status());
        assertEquals("insufficient_scope", shortScope.error());
        assertTrue(shortScope.wwwAuthenticate().contains("scope=\"read admin\""));
        assertThrows(IllegalArgumentException.class, () -> AccessTokenVerifier.builder().issuer(ISSUER).build());
        assertEquals("/.well-known/oauth-protected-resource/v1", AccessTokenVerifier.protectedResourceMetadataPath("https://api.test/v1/"));
        assertEquals(List.of(ISSUER), AccessTokenVerifier.protectedResourceMetadata(AUD, ISSUER + "/", null).get("authorization_servers"));
    }
}
