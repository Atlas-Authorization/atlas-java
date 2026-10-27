package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OAuthClientsConsentRequestsAcceptBody;
import net.atlasauth.atlas.models.OAuthClientsConsentRequestsAcceptResponse;
import net.atlasauth.atlas.models.OAuthClientsConsentRequestsGetResponse;
import net.atlasauth.atlas.models.OAuthClientsLoginRequestsRejectBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/oauth_consent_requests} resource. */
public final class OAuthClientsConsentRequests {

    private final Transport t;

    public OAuthClientsConsentRequests(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/oauth_consent_requests} - Read a consent challenge: the signed-in user, the application and the scopes, audience, authorization details and organization it asks for. Scope: {@code oauth_clients:read}. */
    public OAuthClientsConsentRequestsGetResponse get(String consentChallenge) {
        Map<String, Object> q = Params.of().set("consent_challenge", consentChallenge).map();
        return t.request("GET", "/v1/oauth_consent_requests", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/oauth_consent_requests/accept} - Accept a consent challenge, optionally narrowing the granted scopes. Following redirect_to (in the same browser that started the flow) records the consent unless remember is false, and returns the application to its redirect_uri with a code. Scope: {@code oauth_clients:write}. */
    public OAuthClientsConsentRequestsAcceptResponse accept(String consentChallenge, OAuthClientsConsentRequestsAcceptBody body) {
        Map<String, Object> q = Params.of().set("consent_challenge", consentChallenge).map();
        return t.request("PUT", "/v1/oauth_consent_requests/accept", q, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/oauth_consent_requests/reject} - Reject a consent challenge. Following redirect_to returns the application to its redirect_uri with the OAuth error. Scope: {@code oauth_clients:write}. */
    public OAuthClientsConsentRequestsAcceptResponse reject(String consentChallenge, OAuthClientsLoginRequestsRejectBody body) {
        Map<String, Object> q = Params.of().set("consent_challenge", consentChallenge).map();
        return t.request("PUT", "/v1/oauth_consent_requests/reject", q, body, null, false, new TypeReference<>() {
        });
    }
}
