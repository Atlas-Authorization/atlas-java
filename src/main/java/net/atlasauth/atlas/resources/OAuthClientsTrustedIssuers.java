package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OAuthClientsTrustedIssuersCreateBody;
import net.atlasauth.atlas.models.OAuthClientsTrustedIssuersDeleteResponse;
import net.atlasauth.atlas.models.OAuthClientsTrustedIssuersListResponse;
import net.atlasauth.atlas.models.OAuthClientsTrustedIssuersListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/oauth_clients} resource. */
public final class OAuthClientsTrustedIssuers {

    private final Transport t;

    public OAuthClientsTrustedIssuers(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/oauth_clients/{id}/trusted_issuers} - List the external issuers this OAuth client trusts to assert users through the RFC 7523 jwt-bearer grant. Scope: {@code oauth_clients:read}. */
    public OAuthClientsTrustedIssuersListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public OAuthClientsTrustedIssuersListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/oauth_clients/" + enc(id) + "/trusted_issuers", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/oauth_clients/{id}/trusted_issuers} - Trust an external issuer for this client. With purpose user_assertion (default) its signed JWTs (verified against the inline jwks or the https jwks_uri) can then be presented to POST /oauth2/token with grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer. subject_mapping says how the JWT sub names an Atlas user. With purpose client_auth or both (PRO-06 workload identity) a client registered with token_endpoint_auth_method workload_jwt authenticates by presenting a token from this issuer as its client_assertion; subject_pattern is then required and must name the workload. workload_kind github_actions, gcp and azure fill in the issuer and JWKS URL. Audited as oauth_trusted_issuer.created. Scope: {@code oauth_clients:write}. */
    public OAuthClientsTrustedIssuersListResponseDataItem create(String id, OAuthClientsTrustedIssuersCreateBody body) {
        return create(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public OAuthClientsTrustedIssuersListResponseDataItem create(String id, OAuthClientsTrustedIssuersCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/oauth_clients/" + enc(id) + "/trusted_issuers", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/oauth_clients/{id}/trusted_issuers/{issuerId}} - Stop trusting an issuer: its assertions are refused at once. Audited as oauth_trusted_issuer.deleted. Scope: {@code oauth_clients:write}. */
    public OAuthClientsTrustedIssuersDeleteResponse delete(String id, String issuerId) {
        return t.request("DELETE", "/v1/oauth_clients/" + enc(id) + "/trusted_issuers/" + enc(issuerId), null, null, null, false, new TypeReference<>() {
        });
    }
}
