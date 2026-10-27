package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OAuthClientsLoginRequestsAcceptBody;
import net.atlasauth.atlas.models.OAuthClientsLoginRequestsAcceptResponse;
import net.atlasauth.atlas.models.OAuthClientsLoginRequestsGetResponse;
import net.atlasauth.atlas.models.OAuthClientsLoginRequestsRejectBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/oauth_login_requests} resource. */
public final class OAuthClientsLoginRequests {

    private final Transport t;

    public OAuthClientsLoginRequests(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/oauth_login_requests} - Read a login challenge: which application is signing a user in, with what scopes, and who (if anyone) is already signed in on this browser. Scope: {@code oauth_clients:read}. */
    public OAuthClientsLoginRequestsGetResponse get(String loginChallenge) {
        Map<String, Object> q = Params.of().set("login_challenge", loginChallenge).map();
        return t.request("GET", "/v1/oauth_login_requests", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/oauth_login_requests/accept} - Accept a login challenge: the visitor authenticated as `subject` (an Atlas user id). Returns redirect_to; when the browser follows it Atlas signs the user in and continues the authorization. The returned link is single-use and valid for five minutes. Scope: {@code oauth_clients:write}. */
    public OAuthClientsLoginRequestsAcceptResponse accept(String loginChallenge, OAuthClientsLoginRequestsAcceptBody body) {
        Map<String, Object> q = Params.of().set("login_challenge", loginChallenge).map();
        return t.request("PUT", "/v1/oauth_login_requests/accept", q, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/oauth_login_requests/reject} - Reject a login challenge. Following redirect_to returns the application to its redirect_uri with the OAuth error. Scope: {@code oauth_clients:write}. */
    public OAuthClientsLoginRequestsAcceptResponse reject(String loginChallenge, OAuthClientsLoginRequestsRejectBody body) {
        Map<String, Object> q = Params.of().set("login_challenge", loginChallenge).map();
        return t.request("PUT", "/v1/oauth_login_requests/reject", q, body, null, false, new TypeReference<>() {
        });
    }
}
