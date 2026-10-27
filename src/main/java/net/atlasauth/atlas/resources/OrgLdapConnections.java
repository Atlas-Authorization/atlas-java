package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OrgLdapConnection;
import net.atlasauth.atlas.models.OrgLdapConnectionBody;
import net.atlasauth.atlas.models.OrgLdapConnectionTest;
import net.atlasauth.atlas.models.Organization;

import java.util.List;
import java.util.Map;
import net.atlasauth.atlas.models.OrgLdapConnectionsGetRelayResponse;
import net.atlasauth.atlas.models.OrgLdapConnectionsGetSyncResponse;
import net.atlasauth.atlas.models.OrgLdapConnectionsSetRelayBody;
import net.atlasauth.atlas.models.OrgLdapConnectionsSetSyncBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** Organization-owned LDAP / Active Directory connections ({@code /v1/org_ldap_connections}). */
public final class OrgLdapConnections {

    private final Transport t;

    public OrgLdapConnections(Transport t) {
        this.t = t;
    }

    public ListPage<OrgLdapConnection> list() {
        return list(null);
    }

    /** {@code GET /v1/org_ldap_connections} — List the organization-owned LDAP / AD connections. */
    public ListPage<OrgLdapConnection> list(String organizationId) {
        Map<String, Object> q = Params.of().set("organization_id", organizationId).map();
        return t.get("/v1/org_ldap_connections", q, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/org_ldap_connections} — Create an organization-owned LDAP / AD connection (url, bind_dn, base_dn, user_filter, attributes). */
    public OrgLdapConnection create(OrgLdapConnectionBody body) {
        return t.post("/v1/org_ldap_connections", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OrgLdapConnection create(OrgLdapConnectionBody body, String idempotencyKey) {
        return t.post("/v1/org_ldap_connections", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/org_ldap_connections/{id}} — Fetch one organization-owned LDAP connection. */
    public OrgLdapConnection get(String id) {
        return t.get("/v1/org_ldap_connections/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/org_ldap_connections/{id}} — Update an organization-owned LDAP connection. */
    public OrgLdapConnection update(String id, OrgLdapConnectionBody body) {
        return t.patch("/v1/org_ldap_connections/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/org_ldap_connections/{id}} — Delete an organization-owned LDAP connection. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/org_ldap_connections/" + enc(id), new TypeReference<>() {
        });
    }

    public OrgLdapConnectionTest test(String id) {
        return test(id, null);
    }

    /** {@code POST /v1/org_ldap_connections/{id}/test} — Test an organization-owned LDAP connection: attempt a service-account bind and report ok/failure. */
    public OrgLdapConnectionTest test(String id, String bindPassword) {
        Map<String, Object> payload = Params.of().set("bind_password", bindPassword).map();
        return t.post("/v1/org_ldap_connections/" + enc(id) + "/test", payload, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/org_ldap_connections/{id}/sync} - Read the directory-sync settings of an organization LDAP / Active Directory connection (schedule, filters, group-to-role mapping, disable-on-removal). Scope: {@code sso_connections:read}. */
    public OrgLdapConnectionsGetSyncResponse getSync(String id) {
        return t.request("GET", "/v1/org_ldap_connections/" + enc(id) + "/sync", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/org_ldap_connections/{id}/sync} - Set the directory-sync settings. Fields you omit keep their value. Enabling requires an active connection and schedules the first run immediately; disabling clears the schedule. Role mappings are matched in order, by group DN. Scope: {@code sso_connections:write}. */
    public JsonNode setSync(String id, OrgLdapConnectionsSetSyncBody body) {
        return t.request("PUT", "/v1/org_ldap_connections/" + enc(id) + "/sync", null, body, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/org_ldap_connections/{id}/relay} - Which relay agent an organization LDAP connection reaches its directory through, if any. Scope: {@code sso_connections:read}. */
    public OrgLdapConnectionsGetRelayResponse getRelay(String id) {
        return t.request("GET", "/v1/org_ldap_connections/" + enc(id) + "/relay", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/org_ldap_connections/{id}/relay} - Route an LDAP connection through a relay agent (agent_id), or back to a direct connection (null). The agent must be active and allow the LDAP server. Scope: {@code sso_connections:write}. */
    public JsonNode setRelay(String id, OrgLdapConnectionsSetRelayBody body) {
        return t.request("PUT", "/v1/org_ldap_connections/" + enc(id) + "/relay", null, body, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** The {@code syncRuns} sub-resource. */
    public OrgLdapConnectionsSyncRuns syncRuns() {
        return new OrgLdapConnectionsSyncRuns(t);
    }
}
