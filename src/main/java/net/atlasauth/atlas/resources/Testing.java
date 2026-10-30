package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.TestInstance;
import net.atlasauth.atlas.models.TestUsersPurged;
import net.atlasauth.atlas.models.TestingSignInUrl;
import net.atlasauth.atlas.models.TestingToken;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Test helpers for development instances. */
public final class Testing {

    private final Transport t;

    public Testing(Transport t) {
        this.t = t;
    }

    public TestUsersPurged purgeTestUsers() {
        return purgeTestUsers(null);
    }

    /** {@code POST /v1/test_users/purge} — Dispose test users in bulk (development instances only): soft-delete every user whose email carries a reserved test token (+clerk_test / +atlas_test). */
    public TestUsersPurged purgeTestUsers(Long limit) {
        Map<String, Object> payload = Params.of().set("limit", limit).map();
        return t.post("/v1/test_users/purge", payload, new TypeReference<>() {
        });
    }

    public TestInstance createTestInstance() {
        return createTestInstance(null);
    }

    /** {@code POST /v1/test_instance} — Get (creating on first call) your production instance's paired test-mode (development) sibling — an isolated instance with its own pk_test/sk_test, user pool and JWKS. */
    public TestInstance createTestInstance(Boolean rotateSecret) {
        Map<String, Object> payload = Params.of().set("rotate_secret", rotateSecret).map();
        return t.post("/v1/test_instance", payload, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/testing_tokens} — Mint a short-lived signed testing token (development instances only) that @atlasauth/testing presents to the sign-in gate to skip the human-only CAPTCHA. */
    public TestingToken createTestingToken() {
        return t.post("/v1/testing_tokens", null, new TypeReference<>() {
        });
    }

    /** {@link #createSignInUrl(String, String, Long)} with the default lifetime. */
    public TestingSignInUrl createSignInUrl(String userId, String redirectUrl) {
        return createSignInUrl(userId, redirectUrl, null);
    }

    /**
     * {@code POST /v1/testing/sign_in_url} - a ready one-time URL that signs a browser in as {@code userId} and lands
     * on {@code redirectUrl} (an allowed origin or localhost). Development instances only (403
     * {@code TESTING_UNAVAILABLE} otherwise).
     */
    public TestingSignInUrl createSignInUrl(String userId, String redirectUrl, Long expiresInSeconds) {
        Map<String, Object> payload = Params.of().set("user_id", userId).set("redirect_url", redirectUrl)
                .set("expires_in_seconds", expiresInSeconds).map();
        return t.post("/v1/testing/sign_in_url", payload, new TypeReference<>() {
        });
    }
}
