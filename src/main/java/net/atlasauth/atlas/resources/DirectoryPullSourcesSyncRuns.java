package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DirectoryPullSourcesSyncRunsCreateResponse;
import net.atlasauth.atlas.models.DirectoryPullSourcesSyncRunsListResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/directory_pull_sources} resource. */
public final class DirectoryPullSourcesSyncRuns {

    private final Transport t;

    public DirectoryPullSourcesSyncRuns(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/directory_pull_sources/{id}/sync_runs} - Queue a pull now. One run at a time per source (409 otherwise); the worker executes it. Scope: {@code directory_pull:write}. */
    public DirectoryPullSourcesSyncRunsCreateResponse create(String id) {
        return create(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public DirectoryPullSourcesSyncRunsCreateResponse create(String id, String idempotencyKey) {
        return t.request("POST", "/v1/directory_pull_sources/" + enc(id) + "/sync_runs", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/directory_pull_sources/{id}/sync_runs} - List the runs of a source with counts of people seen, created, re-roled, disabled and re-enabled. Scope: {@code directory_pull:read}. */
    public DirectoryPullSourcesSyncRunsListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public DirectoryPullSourcesSyncRunsListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/directory_pull_sources/" + enc(id) + "/sync_runs", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/directory_pull_sources/{id}/sync_runs/{runId}} - Read one directory pull run with its counts and error. Scope: {@code directory_pull:read}. */
    public DirectoryPullSourcesSyncRunsCreateResponse get(String id, String runId) {
        return t.request("GET", "/v1/directory_pull_sources/" + enc(id) + "/sync_runs/" + enc(runId), null, null, null, false, new TypeReference<>() {
        });
    }
}
