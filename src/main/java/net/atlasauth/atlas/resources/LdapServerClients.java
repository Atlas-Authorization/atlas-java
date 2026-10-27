package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.LdapServerClientsCreateBody;
import net.atlasauth.atlas.models.LdapServerClientsListResponse;
import net.atlasauth.atlas.models.LdapServerClientsListResponseDataItem;
import net.atlasauth.atlas.models.LdapServerClientsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/ldap_server_clients} resource. */
public final class LdapServerClients {

    private final Transport t;

    public LdapServerClients(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/ldap_server_clients} - List the LDAP interface service accounts with their base DN, bind DN and allowed source networks. Secrets are never returned. Scope: {@code ldap_server:read}. */
    public LdapServerClientsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public LdapServerClientsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/ldap_server_clients", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/ldap_server_clients} - Create a service account for the LDAPS interface: base_dn (dc=/o=/ou= parts, unique across Atlas), bind_cn, allowed_cidrs (empty = nobody may connect), allow_user_bind and size_limit. The secret is returned once as secret. Scope: {@code ldap_server:write}. */
    public LdapServerClientsListResponseDataItem create(LdapServerClientsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public LdapServerClientsListResponseDataItem create(LdapServerClientsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/ldap_server_clients", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/ldap_server_clients/{id}} - Read one LDAP interface service account. Scope: {@code ldap_server:read}. */
    public LdapServerClientsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/ldap_server_clients/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/ldap_server_clients/{id}} - Change the name, allowed networks, user-bind switch, size limit or status (active or disabled). Scope: {@code ldap_server:write}. */
    public LdapServerClientsListResponseDataItem update(String id, LdapServerClientsUpdateBody body) {
        return t.request("PATCH", "/v1/ldap_server_clients/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/ldap_server_clients/{id}} - Delete a service account; its connections can no longer bind. Scope: {@code ldap_server:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/ldap_server_clients/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/ldap_server_clients/{id}/rotate_secret} - Issue a new service-account secret (returned once); the old one stops working immediately. Scope: {@code ldap_server:write}. */
    public LdapServerClientsListResponseDataItem rotateSecret(String id) {
        return rotateSecret(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public LdapServerClientsListResponseDataItem rotateSecret(String id, String idempotencyKey) {
        return t.request("POST", "/v1/ldap_server_clients/" + enc(id) + "/rotate_secret", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
