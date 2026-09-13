package net.atlasauth.atlas;

import net.atlasauth.atlas.handshake.Handshake;
import net.atlasauth.atlas.handshake.HandshakeRedirect;
import net.atlasauth.atlas.handshake.HandshakeSession;
import net.atlasauth.atlas.handshake.Pkce;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.MintedSession;
import net.atlasauth.atlas.models.Organization;
import net.atlasauth.atlas.models.Role;
import net.atlasauth.atlas.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end resource tests over a loopback {@link MockBackend}: they assert both
 * the request the SDK shapes (verb, path, auth, idempotency, JSON body) and the
 * typed value it decodes back from a snake_case wire response.
 */
class AtlasClientTest {

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder()
                .secretKey("sk_test_123")
                .apiUrl(mock.baseUrl())
                .build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    @Test
    void getUserDecodesSnakeCaseAndSendsBearer() {
        mock.reply(200, """
                {
                  "object": "user",
                  "id": "usr_123",
                  "username": "ada",
                  "first_name": "Ada",
                  "last_name": "Lovelace",
                  "image_url": "https://img/a.png",
                  "public_metadata": {"plan": "pro"},
                  "mfa_enabled": true,
                  "banned": false,
                  "locked": false,
                  "last_sign_in_at": 1700000000000,
                  "created_at": 1699990000000,
                  "updated_at": 1699995000000
                }
                """);

        User user = atlas.users().get("usr_123");

        assertEquals("GET", mock.method);
        assertEquals("/v1/users/usr_123", mock.path);
        assertEquals("Bearer sk_test_123", mock.authorization);
        assertEquals("usr_123", user.id());
        assertEquals("Ada", user.firstName());
        assertTrue(user.mfaEnabled());
        assertEquals("pro", user.publicMetadata().get("plan"));
        assertEquals(1700000000000L, user.lastSignInAt());
    }

    @Test
    void createUserSendsJsonBodyAndIdempotencyKey() {
        mock.reply(200, """
                {"object":"user","id":"usr_new","created_at":1,"updated_at":1}
                """);

        CreateUserBody body = new CreateUserBody();
        body.emailAddress = "grace@example.com";
        body.firstName = "Grace";

        User user = atlas.users().create(body, "idem-key-1");

        assertEquals("POST", mock.method);
        assertEquals("/v1/users", mock.path);
        assertEquals("idem-key-1", mock.idempotencyKey);
        assertTrue(mock.requestBody.contains("\"email_address\":\"grace@example.com\""));
        // NON_NULL: unset optionals are omitted from the wire body.
        assertFalse(mock.requestBody.contains("last_name"));
        // camelCase field bridged to snake_case on the wire.
        assertTrue(mock.requestBody.contains("\"first_name\":\"Grace\""));
        assertEquals("usr_new", user.id());
    }

    @Test
    void listUsersSerializesQueryParams() {
        mock.reply(200, """
                {"data":[{"object":"user","id":"usr_1","created_at":1,"updated_at":1}],
                 "has_more":true,"next_cursor":"usr_1"}
                """);

        CursorPage<User> page = atlas.users().list(10, "usr_0");

        assertEquals("/v1/users", mock.path);
        assertNotNull(mock.rawQuery);
        assertTrue(mock.rawQuery.contains("limit=10"));
        assertTrue(mock.rawQuery.contains("starting_after=usr_0"));
        assertTrue(page.hasMore());
        assertEquals("usr_1", page.nextCursor());
        assertEquals(1, page.data().size());
    }

    @Test
    void createSessionMintsBearerAndRefreshToken() {
        mock.reply(200, """
                {"object":"session","id":"sess_1","user_id":"usr_1",
                 "jwt":"ey.header.sig","refresh_token":"rt_abc","expires_in":60}
                """);

        MintedSession session = atlas.sessions().create("usr_1");

        assertEquals("POST", mock.method);
        assertEquals("/v1/sessions", mock.path);
        assertTrue(mock.requestBody.contains("\"user_id\":\"usr_1\""));
        assertEquals("ey.header.sig", session.jwt());
        assertEquals("rt_abc", session.refreshToken());
        assertEquals(60L, session.expiresIn());
    }

    @Test
    void listOrganizationsReturnsCursorPage() {
        mock.reply(200, """
                {"data":[{"object":"organization","id":"org_1","name":"Acme","slug":"acme",
                          "max_allowed_memberships":5,"created_at":1,"updated_at":1}],
                 "has_more":false,"next_cursor":null}
                """);

        CursorPage<Organization> page = atlas.organizations().list();

        assertEquals("GET", mock.method);
        assertEquals("/v1/organizations", mock.path);
        assertFalse(page.hasMore());
        assertNull(page.nextCursor());
        assertEquals("Acme", page.data().get(0).name());
    }

    @Test
    void listRolesReturnsListPage() {
        mock.reply(200, """
                {"object":"list","data":[
                  {"object":"role","id":"role_1","key":"admin","name":"Admin",
                   "is_system":true,"permissions":["org:reports:read"],"member_count":3,"created_at":1}
                ]}
                """);

        List<Role> roles = atlas.roles().list().data();

        assertEquals("/v1/roles", mock.path);
        assertEquals("admin", roles.get(0).key());
        assertTrue(roles.get(0).isSystem());
        assertEquals(List.of("org:reports:read"), roles.get(0).permissions());
    }

    @Test
    void deleteRoleForwardsReassignQuery() {
        mock.reply(200, """
                {"object":"role","id":"role_1","deleted":true}
                """);

        atlas.roles().delete("role_1", "role_fallback");

        assertEquals("DELETE", mock.method);
        assertEquals("/v1/roles/role_1", mock.path);
        assertTrue(mock.rawQuery.contains("reassign_to=role_fallback"));
    }

    @Test
    void notFoundRaisesTypedNotFoundException() {
        mock.reply(404, """
                {"errors":[{"code":"resource_not_found","message":"No such user"}]}
                """);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> atlas.users().get("usr_missing"));

        assertEquals(404, ex.status());
        assertEquals("resource_not_found", ex.code());
        assertTrue(ex.hasCode("resource_not_found"));
        assertEquals("No such user", ex.getMessage());
    }

    @Test
    void unprocessableRaisesInvalidRequestException() {
        mock.reply(422, """
                {"errors":[{"code":"form_param_missing","message":"username is required"}]}
                """);

        AtlasApiException ex = assertThrows(InvalidRequestException.class,
                () -> atlas.users().create(new CreateUserBody()));

        assertEquals(422, ex.status());
        assertEquals("form_param_missing", ex.code());
    }

    @Test
    void unauthorizedRaisesAuthenticationException() {
        mock.reply(401, """
                {"errors":[{"code":"authentication_invalid","message":"Bad key"}]}
                """);

        assertThrows(AuthenticationException.class, () -> atlas.users().get("usr_1"));
    }

    @Test
    void missingSecretKeyIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> AtlasClient.builder().build());
    }

    // --- Cross-property SSO handshake (PKCE-bound) ---------------------------------

    @Test
    void generatePkceProducesS256ChallengeWithoutPadding() throws NoSuchAlgorithmException {
        Pkce pkce = Handshake.generatePkce();

        // Both halves are base64url with NO padding (RFC 7636).
        assertFalse(pkce.verifier().contains("="), "verifier must be unpadded");
        assertFalse(pkce.challenge().contains("="), "challenge must be unpadded");
        assertFalse(pkce.verifier().contains("+") || pkce.verifier().contains("/"),
                "verifier must be URL-safe base64");
        assertFalse(pkce.challenge().contains("+") || pkce.challenge().contains("/"),
                "challenge must be URL-safe base64");

        // Verifier decodes back to the 32 SecureRandom bytes.
        byte[] verifierBytes = Base64.getUrlDecoder().decode(pkce.verifier());
        assertEquals(32, verifierBytes.length);

        // challenge == base64url( SHA-256( US-ASCII(verifier) ) ), no padding.
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(pkce.verifier().getBytes(StandardCharsets.US_ASCII));
        String expectedChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        assertEquals(expectedChallenge, pkce.challenge());

        // And the decoded challenge is exactly the 32-byte SHA-256 digest.
        assertArrayEquals(digest, Base64.getUrlDecoder().decode(pkce.challenge()));

        // Fresh pair each call.
        assertFalse(pkce.verifier().equals(Handshake.generatePkce().verifier()));
    }

    @Test
    void handshakeRedirectPutsChallengeInUrlAndKeepsVerifierOut() throws NoSuchAlgorithmException {
        HandshakeRedirect redirect = Handshake.handshakeRedirect(
                "https://id.atlasauth.net/", "pk_live_abc", "https://docs.example.com/page?a=b");

        assertTrue(redirect.url().startsWith("https://id.atlasauth.net/v1/client/handshake?"),
                "trailing slash on origin is trimmed");
        assertTrue(redirect.url().contains("publishable_key=pk_live_abc"));
        // redirect_url is form-url-encoded.
        assertTrue(redirect.url().contains("redirect_url=https%3A%2F%2Fdocs.example.com%2Fpage%3Fa%3Db"));

        // The one-way challenge rides the URL; the secret verifier never does.
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(redirect.codeVerifier().getBytes(StandardCharsets.US_ASCII));
        String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        assertTrue(redirect.url().contains("code_challenge=" + challenge),
                "url carries the S256 challenge for the returned verifier");
        assertFalse(redirect.url().contains(redirect.codeVerifier()),
                "the verifier secret must never appear in the URL");
        assertFalse(redirect.url().contains("code_verifier"));
    }

    @Test
    void redeemHandshakeSendsCodeVerifierInBody() {
        mock.reply(200, """
                {"jwt":"ey.header.sig","refresh_token":"rt_abc","session_id":"sess_9","expires_in":60}
                """);

        HandshakeSession session = Handshake.redeemHandshake(
                mock.baseUrl(), "pk_live_abc", "usr_1", "nonce_xyz", "ver_test_123");

        assertEquals("POST", mock.method);
        assertEquals("/v1/client/handshake/redeem", mock.path);
        assertTrue(mock.requestBody.contains("\"user_id\":\"usr_1\""));
        assertTrue(mock.requestBody.contains("\"nonce\":\"nonce_xyz\""));
        assertTrue(mock.requestBody.contains("\"code_verifier\":\"ver_test_123\""),
                "redeem body must carry the PKCE verifier");

        assertNotNull(session);
        assertEquals("ey.header.sig", session.jwt());
        assertEquals("rt_abc", session.refreshToken());
        assertEquals("sess_9", session.sessionId());
        assertEquals(60L, session.expiresIn());
    }

    @Test
    void redeemHandshakeReturnsNullWhenVerifierMissing() {
        // The contract: an auth failure (here, a null verifier) is a signed-out
        // signal, never an exception. No request should even be attempted.
        HandshakeSession session = Handshake.redeemHandshake(
                "https://id.atlasauth.net", "pk_live_abc", "usr_1", "nonce_xyz", null);
        assertNull(session);
    }
}
