package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.EmailAddress;
import net.atlasauth.atlas.models.Identity;
import net.atlasauth.atlas.models.Grant;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OAuthAccessToken;
import net.atlasauth.atlas.models.ReplaceUserMetadataBody;
import net.atlasauth.atlas.models.UpdateUserBody;
import net.atlasauth.atlas.models.User;
import net.atlasauth.atlas.models.UserSession;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class Users {

    private final Transport t;

    public Users(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/users} — cursor-paginated. */
    public CursorPage<User> list() {
        return list(null, null);
    }

    public CursorPage<User> list(Integer limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/users", q, new TypeReference<>() {
        });
    }

    public User get(String id) {
        return t.get("/v1/users/" + enc(id), new TypeReference<>() {
        });
    }

    public User create(CreateUserBody body) {
        return create(body, null);
    }

    public User create(CreateUserBody body, String idempotencyKey) {
        return t.post("/v1/users", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public User update(String id, UpdateUserBody body) {
        return t.patch("/v1/users/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/users/:id/metadata} — replaces the named bags wholesale. */
    public User replaceMetadata(String id, ReplaceUserMetadataBody body) {
        return t.put("/v1/users/" + enc(id) + "/metadata", body, new TypeReference<>() {
        });
    }

    public User ban(String id) {
        return t.post("/v1/users/" + enc(id) + "/ban", null, new TypeReference<>() {
        });
    }

    public User unban(String id) {
        return t.post("/v1/users/" + enc(id) + "/unban", null, new TypeReference<>() {
        });
    }

    public User lock(String id) {
        return lock(id, null);
    }

    public User lock(String id, Integer durationInSeconds) {
        Map<String, Object> body = Params.of().set("duration_in_seconds", durationInSeconds).map();
        return t.post("/v1/users/" + enc(id) + "/lock", body, new TypeReference<>() {
        });
    }

    public User unlock(String id) {
        return t.post("/v1/users/" + enc(id) + "/unlock", null, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/users/" + enc(id), new TypeReference<>() {
        });
    }

    public JsonNode resetMfa(String id) {
        return t.post("/v1/users/" + enc(id) + "/reset_mfa", null, new TypeReference<>() {
        });
    }

    public JsonNode deleteMfaFactor(String id, String factorId) {
        return t.delete("/v1/users/" + enc(id) + "/mfa/" + enc(factorId), new TypeReference<>() {
        });
    }

    public ListPage<UserSession> listSessions(String id) {
        return t.get("/v1/users/" + enc(id) + "/sessions", new TypeReference<>() {
        });
    }

    public JsonNode revokeSessions(String id) {
        return t.post("/v1/users/" + enc(id) + "/sessions/revoke", null, new TypeReference<>() {
        });
    }

    public EmailAddress addEmail(String id, String emailAddress) {
        Map<String, Object> body = Params.of("email_address", emailAddress).map();
        return t.post("/v1/users/" + enc(id) + "/email_addresses", body, new TypeReference<>() {
        });
    }

    public EmailAddress verifyEmail(String id, String emailId) {
        return t.post("/v1/users/" + enc(id) + "/email_addresses/" + enc(emailId) + "/verify", null,
                new TypeReference<>() {
                });
    }

    public EmailAddress setPrimaryEmail(String id, String emailId) {
        return t.post("/v1/users/" + enc(id) + "/email_addresses/" + enc(emailId) + "/primary", null,
                new TypeReference<>() {
                });
    }

    /** {@code GET /v1/users/:id/oauth_access_tokens/:provider} — a live provider credential. */
    public OAuthAccessToken getOAuthAccessToken(String id, String provider) {
        return t.get("/v1/users/" + enc(id) + "/oauth_access_tokens/" + enc(provider), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/:id/identities} — base Atlas identity plus linked providers. */
    public ListPage<Identity> listIdentities(String id) {
        return t.get("/v1/users/" + enc(id) + "/identities", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/:id/identities} — merge a secondary user INTO this one. */
    public JsonNode linkIdentity(String id, String secondaryUserId, String idempotencyKey) {
        Map<String, Object> body = Params.of("secondary_user_id", secondaryUserId).map();
        return t.post("/v1/users/" + enc(id) + "/identities", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/:id/external_accounts/connect} — backend-initiated provider link. */
    public JsonNode connectExternalAccount(String id, String provider, String redirectUrl,
                                           List<String> additionalScopes, String idempotencyKey) {
        Map<String, Object> body = Params.of()
                .set("provider", provider)
                .set("redirect_url", redirectUrl)
                .set("additional_scopes", additionalScopes)
                .map();
        return t.post("/v1/users/" + enc(id) + "/external_accounts/connect", body, idempotencyKey,
                new TypeReference<>() {
                });
    }

    /** {@code DELETE /v1/users/:id/identities/:identityId} — extract a linked identity into a new user. */
    public JsonNode unlinkIdentity(String id, String identityId) {
        return t.delete("/v1/users/" + enc(id) + "/identities/" + enc(identityId), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/:id/grants} — the OAuth clients this user has authorized. */
    public ListPage<Grant> listGrants(String id) {
        return t.get("/v1/users/" + enc(id) + "/grants", new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/:id/grants} — revoke every consent grant the user holds. */
    public JsonNode revokeAllGrants(String id) {
        return t.delete("/v1/users/" + enc(id) + "/grants", new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/grants/:id} — revoke ONE consent grant (instance-scoped). */
    public JsonNode revokeGrant(String grantId) {
        return t.delete("/v1/grants/" + enc(grantId), new TypeReference<>() {
        });
    }
}
