package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ConnectionsOauthClientsListResponse;
import net.atlasauth.atlas.models.ConnectionsOauthClientsSetBody;
import net.atlasauth.atlas.models.ConnectionsOauthClientsSetResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/connections} resource. */
public final class ConnectionsOauthClients {

    private final Transport t;

    public ConnectionsOauthClients(Transport t) {
        this.t = t;
    }

    /** {@code PUT /v1/connections/oauth_clients} - Set an owner's own OAuth client (their registered app) for a provider and environment. It overrides the instance client for that owner's connections to connect and to refresh. Write-only secret, sealed in the vault. Creates the client or replaces it. Scope: {@code organizations:write}. */
    public ConnectionsOauthClientsSetResponse set(ConnectionsOauthClientsSetBody body) {
        return t.request("PUT", "/v1/connections/oauth_clients", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections/oauth_clients} - List owner OAuth clients. The secret is never returned. Scope: {@code organizations:read}. */
    public ConnectionsOauthClientsListResponse list() {
        return list(null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public ConnectionsOauthClientsListResponse list(String ownerType, String ownerId, String provider, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("owner_type", ownerType).set("owner_id", ownerId).set("provider", provider).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/connections/oauth_clients", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections/oauth_clients/{id}} - Read one owner OAuth client. Scope: {@code organizations:read}. */
    public ConnectionsOauthClientsSetResponse get(String id) {
        return t.request("GET", "/v1/connections/oauth_clients/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/connections/oauth_clients/{id}} - Delete an owner OAuth client and its sealed secret. 409 while connections still hold tokens it issued (they could no longer refresh). Scope: {@code organizations:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/connections/oauth_clients/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
