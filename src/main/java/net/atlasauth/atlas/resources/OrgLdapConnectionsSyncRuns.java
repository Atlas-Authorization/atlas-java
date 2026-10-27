package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OrgLdapConnectionsSyncRunsCreateBody;
import net.atlasauth.atlas.models.OrgLdapConnectionsSyncRunsListResponse;
import net.atlasauth.atlas.models.OrgLdapConnectionsSyncRunsListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/org_ldap_connections} resource. */
public final class OrgLdapConnectionsSyncRuns {

    private final Transport t;

    public OrgLdapConnectionsSyncRuns(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/org_ldap_connections/{id}/sync_runs} - Queue a sync run now (202). `full` (default) lists the whole directory and, when disable-on-removal is on, bans users that left it; `delta` only reads entries changed since the last successful run. Refused with 409 while another run is queued or running. Scope: {@code sso_connections:write}. */
    public JsonNode create(String id, OrgLdapConnectionsSyncRunsCreateBody body) {
        return create(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode create(String id, OrgLdapConnectionsSyncRunsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/org_ldap_connections/" + enc(id) + "/sync_runs", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/org_ldap_connections/{id}/sync_runs} - The sync run log, newest first, with per-run counts (seen, created, updated, skipped, disabled, role changes, groups, errors) and any failure message. Scope: {@code sso_connections:read}. */
    public OrgLdapConnectionsSyncRunsListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public OrgLdapConnectionsSyncRunsListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/org_ldap_connections/" + enc(id) + "/sync_runs", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/org_ldap_connections/{id}/sync_runs/{runId}} - Read one LDAP directory sync run with its counts and error. Scope: {@code sso_connections:read}. */
    public OrgLdapConnectionsSyncRunsListResponseDataItem get(String id, String runId) {
        return t.request("GET", "/v1/org_ldap_connections/" + enc(id) + "/sync_runs/" + enc(runId), null, null, null, false, new TypeReference<>() {
        });
    }
}
