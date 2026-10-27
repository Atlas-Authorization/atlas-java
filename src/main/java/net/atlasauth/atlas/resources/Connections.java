package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ConnectionsConnectBody;
import net.atlasauth.atlas.models.ConnectionsConnectResponse;
import net.atlasauth.atlas.models.ConnectionsCreateBody;
import net.atlasauth.atlas.models.ConnectionsCreateResponse;
import net.atlasauth.atlas.models.ConnectionsExportConnectionsBody;
import net.atlasauth.atlas.models.ConnectionsExportConnectionsResponse;
import net.atlasauth.atlas.models.ConnectionsImportConnectionsBody;
import net.atlasauth.atlas.models.ConnectionsImportConnectionsResponse;
import net.atlasauth.atlas.models.ConnectionsListResponse;
import net.atlasauth.atlas.models.ConnectionsOauth1SignBody;
import net.atlasauth.atlas.models.ConnectionsOauth1SignResponse;
import net.atlasauth.atlas.models.ConnectionsRefreshTokenBody;
import net.atlasauth.atlas.models.ConnectionsRelayBody;
import net.atlasauth.atlas.models.ConnectionsRelayResponse;
import net.atlasauth.atlas.models.ConnectionsRotateCredentialsBody;
import net.atlasauth.atlas.models.ConnectionsSecretResponse;
import net.atlasauth.atlas.models.ConnectionsSecretVersionsResponse;
import net.atlasauth.atlas.models.ConnectionsSetCredentialsBody;
import net.atlasauth.atlas.models.ConnectionsTokenResponse;
import net.atlasauth.atlas.models.ConnectionsTokensBody;
import net.atlasauth.atlas.models.ConnectionsTokensResponse;
import net.atlasauth.atlas.models.ConnectionsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/connections} resource. */
public final class Connections {

    private final Transport t;

    public Connections(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/connections} - Define a connection: an owner (an organization or a user) and a third-party API. `kind` is oauth (rides a linked provider account; for an organization, the account of the member who shared it), api_key or client_credentials (a credential the owner enters). `allowed_hosts` lists the only hosts the relay may call. An owner may hold several connections of one provider (one per provider account): create one per account, then call `POST /v1/connections/&#123;id&#125;/connect`. A connection is never a sign-in method. Scope: {@code organizations:write}. */
    public ConnectionsCreateResponse create(ConnectionsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsCreateResponse create(ConnectionsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections} - List connections, newest first, with each one's live status. Filter by owner or provider. Scope: {@code organizations:read}. */
    public ConnectionsListResponse list() {
        return list(null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public ConnectionsListResponse list(String ownerType, String ownerId, String provider, String environment, String status, String startingAfter, Long limit) {
        Map<String, Object> q = Params.of().set("owner_type", ownerType).set("owner_id", ownerId).set("provider", provider).set("environment", environment).set("status", status).set("starting_after", startingAfter).set("limit", limit).map();
        return t.request("GET", "/v1/connections", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections/{id}} - Read one connection with its current status. Scope: {@code organizations:read}. */
    public ConnectionsCreateResponse get(String id) {
        return t.request("GET", "/v1/connections/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/connections/{id}} - Rename a connection, change its allowed hosts or settings, transfer it to another owner with `owner` (the stored tokens move with it, no reconnect), change `environment` or `host_params` (an oauth connection that already holds a token becomes needs_reauth, because the token belongs to the old host), or (organization-owned oauth) set `shared_by_user_id` to copy that member's linked account into the connection. Scope: {@code organizations:write}. */
    public ConnectionsCreateResponse update(String id, ConnectionsUpdateBody body) {
        return t.request("PATCH", "/v1/connections/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/connections/{id}} - Delete a connection and its stored credential (204). With `revoke=true` the token is first revoked at the provider and the answer is 200 with what the provider said (provider_revoked true, false or "unsupported", provider_error, local_cleared); the connection is deleted either way. Scope: {@code organizations:write}. */
    public JsonNode delete(String id) {
        return delete(id, null);
    }

    /** As above, with every optional parameter. */
    public JsonNode delete(String id, Boolean revoke) {
        Map<String, Object> q = Params.of().set("revoke", revoke).map();
        return t.request("DELETE", "/v1/connections/" + enc(id), q, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code PUT /v1/connections/{id}/credentials} - Enter the credential for a non-OAuth connection (api_key, bearer, basic, custom_header, hmac_secret, client_credentials, jwt_bearer, oauth1, github_app_installation). Write-only: it is sealed in the vault and never returned. Replaces the earlier credential as a new vault version; use POST .../credentials/rotate to keep the old version valid for an overlap window. Scope: {@code organizations:write}. */
    public ConnectionsCreateResponse setCredentials(String id, ConnectionsSetCredentialsBody body) {
        return t.request("PUT", "/v1/connections/" + enc(id) + "/credentials", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/{id}/connect} - Start the provider authorization for this connection and return the URL to send the owner to. The tokens are stored on the connection itself, never on a user: no external account is created, no sign-in method is added, no email is proven and no step-up is needed. `host_params` fill the provider's host templates for this connection (validated against a per-provider allowlist; 400 `host_params.&lt;key&gt;` otherwise). Reconnecting the same provider account updates the same connection; a different account answers 409 unless `create_if_new` is true. An owner OAuth client (`PUT /v1/connections/oauth_clients`) overrides the instance client. Scope: {@code organizations:write}. */
    public ConnectionsConnectResponse connect(String id, ConnectionsConnectBody body) {
        return connect(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsConnectResponse connect(String id, ConnectionsConnectBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/connect", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/{id}/revoke} - Revoke the stored token at the provider, delete it from the vault and mark the connection revoked (connection.revoked is sent once). The response is the connection plus what the provider said: provider_revoked is true only when it confirmed, "unsupported" when it has no revoke endpoint, false when it refused or was unreachable (provider_error says why). The local token is cleared in every case. Connect again to use it. Scope: {@code organizations:write}. */
    public JsonNode revoke(String id, Map<String, Object> body) {
        return revoke(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode revoke(String id, Map<String, Object> body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/revoke", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/connections/{id}/oauth1/sign} - Have Atlas sign a request with an oauth1 connection (NetSuite token-based authentication: HMAC-SHA256 with the realm; X Ads and other three-legged providers: HMAC-SHA1) and return the `Authorization: OAuth ...` header. The consumer and token secrets never leave Atlas. The URL must be https on an allowed host; a form-encoded body is part of the signature. Needs connections:sign. Scope: {@code connections:sign}. */
    public ConnectionsOauth1SignResponse oauth1Sign(String id, ConnectionsOauth1SignBody body) {
        return oauth1Sign(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsOauth1SignResponse oauth1Sign(String id, ConnectionsOauth1SignBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/oauth1/sign", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/{id}/relay} - Make a provider API call server-side with this connection's credential, so the credential never reaches your code: Atlas injects an api_key (header or query), bearer, basic or custom header, signs with an hmac_secret or OAuth 1.0a, or sends a minted client_credentials / jwt_bearer / GitHub App installation / admin consent token. Only https calls to the connection's allowed_hosts are made, with the verbs in allowed_methods (default GET, HEAD, POST). When the connection is not connected, or the provider rejects the credential, the answer is 402 CONNECTION_REQUIRED with `meta.reason` (not_connected or needs_reauth) and, for a user-owned OAuth connection when you pass `redirect_url`, `meta.authorize_url`. The audit log records the method, host and status only. Scope: {@code oauth_tokens:read}. */
    public ConnectionsRelayResponse relay(String id, ConnectionsRelayBody body) {
        return relay(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsRelayResponse relay(String id, ConnectionsRelayBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/relay", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections/{id}/token} - The connection's live token for your backend. An oauth connection's token is refreshed first when it is expired or about to be (single-flight across processes, using the client that issued it and the connection's host_params). Built for sync workers: the response carries an `ETag` and `Cache-Control: private, max-age=&lt;seconds until just before expires_at&gt;`, so cache it until then; send `If-None-Match` and an unchanged, still-valid token answers 304 with no body and no vault read; `min_ttl_seconds` refreshes first when less than that much validity remains. A grant connection (client_credentials, jwt_bearer, github_app_installation, admin_consent) has its token minted from the credential Atlas holds instead, answered with `Cache-Control: no-store` (no ETag); pass `subject` to a jwt_bearer connection to act as that user (Google domain-wide delegation). Includes the captured handles (`metadata`, `token_response`) and any further named credentials. 402 CONNECTION_REQUIRED with `meta.reason` not_connected, needs_reauth, revoked or refresh_failed when there is no usable token. When the provider answers 401 to the token, call `POST /v1/connections/&#123;id&#125;/token/refresh` instead of re-reading. Rate limit: 6,000 reads per minute per secret key and 300 per minute per connection (RateLimit-* headers on every response, 429 with Retry-After beyond them). Reads are audited as connection.token_read once per connection per minute with the read count (a read that refreshed is always audited); the token itself is never logged. Scope: {@code connections:token}. */
    public ConnectionsTokenResponse token(String id) {
        return token(id, null, null, null);
    }

    /** As above, with every optional parameter. */
    public ConnectionsTokenResponse token(String id, Long minTtlSeconds, String subject, String refresh) {
        Map<String, Object> q = Params.of().set("min_ttl_seconds", minTtlSeconds).set("subject", subject).set("refresh", refresh).map();
        return t.request("GET", "/v1/connections/" + enc(id) + "/token", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/{id}/token/refresh} - Force a refresh after the provider refused the access token (401). Single-flight: however many callers saw the same 401, the provider is called once. Send `stale_token_hash` (lowercase hex SHA-256 of the access token you were refused with); if the stored token is already a different one, a peer has refreshed and the current token is returned without calling the provider (`refreshed: false`). Same body, ETag and 402 behaviour as the token read; the response is never cached. Counts against the same rate limit as the token read. Scope: {@code connections:token}. */
    public ConnectionsTokenResponse refreshToken(String id, ConnectionsRefreshTokenBody body) {
        return refreshToken(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsTokenResponse refreshToken(String id, ConnectionsRefreshTokenBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/token/refresh", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/tokens/batch} - Read the tokens of up to 100 connections in one call (min_ttl_seconds applies to each). Each id gets its own result: status ok with the token, or status error with a code and reason (not_connected, needs_reauth, revoked, refresh_failed, unknown_connection), so one dead connection never fails the batch. The response is never cached. Counts once against the token-read rate limit. Scope: {@code connections:token}. */
    public ConnectionsTokensResponse tokens(ConnectionsTokensBody body) {
        return tokens(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsTokensResponse tokens(ConnectionsTokensBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/tokens/batch", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/import} - Bulk-import existing provider refresh tokens as connections, up to 500 rows, so nobody reconnects. Each row: provider, owner, external_id (the provider-side account: how a re-import finds the same connection), refresh_token (or access_token), and optionally scopes, metadata, environment, host_params, name, allowed_hosts. By default (validate true) each refresh token is spent once against the provider with the OAuth client that will own the connection, and the tokens the provider issues are stored, so the imported copy stops being current on a rotating provider. A failing row creates and changes nothing. dry_run checks shapes only and never calls the provider. Idempotent on Idempotency-Key. Tokens go straight to the vault and never appear in the answer or the audit log. Scope: {@code connections:import}. */
    public ConnectionsImportConnectionsResponse importConnections(ConnectionsImportConnectionsBody body) {
        return importConnections(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsImportConnectionsResponse importConnections(ConnectionsImportConnectionsBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/import", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/export} - Export connections. Without include_secrets: metadata only (provider, owner, account, environment, scopes, status), scope connections:export. With include_secrets: needs scope connections:export_secrets, the header X-Atlas-Confirm: export-secrets and recipient_public_key (a public JWK: RSA of 3072 bits or more, or EC P-256/384/521). The tokens come back as one compact JWE sealed to that key that only its holder can open; Atlas keeps no plaintext. Pages hold up to 200 connections with secrets (500 without); follow next_cursor with starting_after. Every export is audited with its count, never the secrets. Opened, each connection is a valid import row. Scope: {@code connections:export}. */
    public ConnectionsExportConnectionsResponse exportConnections(ConnectionsExportConnectionsBody body) {
        return exportConnections(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsExportConnectionsResponse exportConnections(ConnectionsExportConnectionsBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/export", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections/{id}/secret} - Read the credential an api_key, bearer, basic, custom_header or hmac_secret connection stores, for your backend. Needs the dedicated connections:secret_read scope; every read is audited as connection.secret.read with the caller, connection and version (never the value) and the response is `Cache-Control: no-store`. `version` is current (default), previous (only while a rotation's overlap window is open) or a version number. A signing grant's private key is never readable: 409 `not_readable`. There is no browser or session route that reads a credential. Scope: {@code connections:secret_read}. */
    public ConnectionsSecretResponse secret(String id) {
        return secret(id, null);
    }

    /** As above, with every optional parameter. */
    public ConnectionsSecretResponse secret(String id, String version) {
        Map<String, Object> q = Params.of().set("version", version).map();
        return t.request("GET", "/v1/connections/" + enc(id) + "/secret", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections/{id}/credentials/versions} - The vault versions of a connection's entered credential (numbers and times only, never values) and the open rotation window. Scope: {@code organizations:read}. */
    public ConnectionsSecretVersionsResponse secretVersions(String id) {
        return secretVersions(id, null, null);
    }

    /** As above, with every optional parameter. */
    public ConnectionsSecretVersionsResponse secretVersions(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/connections/" + enc(id) + "/credentials/versions", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/{id}/credentials/rotate} - Rotate the credential: the body is the new credential (the same fields as PUT credentials). It becomes the current vault version; the replaced one stays valid for `overlap_seconds` (default 3600, up to 30 days): the relay falls back to it when the provider refuses the new one, and GET secret?version=previous returns it. `expires_at` sets the new credential's expiry. The secret never reaches the audit log. Scope: {@code organizations:write}. */
    public ConnectionsCreateResponse rotateCredentials(String id, ConnectionsRotateCredentialsBody body) {
        return rotateCredentials(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsCreateResponse rotateCredentials(String id, ConnectionsRotateCredentialsBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/credentials/rotate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** The {@code oauthClients} sub-resource. */
    public ConnectionsOauthClients oauthClients() {
        return new ConnectionsOauthClients(t);
    }

    /** The {@code webhooks} sub-resource. */
    public ConnectionsWebhooks webhooks() {
        return new ConnectionsWebhooks(t);
    }
}
