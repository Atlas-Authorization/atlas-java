package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DirectoryPullSourcesCreateBody;
import net.atlasauth.atlas.models.DirectoryPullSourcesListResponse;
import net.atlasauth.atlas.models.DirectoryPullSourcesListResponseDataItem;
import net.atlasauth.atlas.models.DirectoryPullSourcesSetCsvBody;
import net.atlasauth.atlas.models.DirectoryPullSourcesUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/directory_pull_sources} resource. */
public final class DirectoryPullSources {

    private final Transport t;

    public DirectoryPullSources(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/directory_pull_sources} - List HR / directory pull sources, optionally for one organization (organization_id). Credentials are never returned. Scope: {@code directory_pull:read}. */
    public DirectoryPullSourcesListResponse list() {
        return list(null, null, null);
    }

    /** As above, with every optional parameter. */
    public DirectoryPullSourcesListResponse list(String organizationId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("organization_id", organizationId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/directory_pull_sources", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/directory_pull_sources} - Create a pull source for an organization: provider (entra, google_workspace, bamboohr, hibob, personio, csv, rest_json), config, write-only credentials and settings (schedule, role mappings, disable on removal). Scope: {@code directory_pull:write}. */
    public DirectoryPullSourcesListResponseDataItem create(DirectoryPullSourcesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public DirectoryPullSourcesListResponseDataItem create(DirectoryPullSourcesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/directory_pull_sources", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/directory_pull_sources/{id}} - Read one pull source. Scope: {@code directory_pull:read}. */
    public DirectoryPullSourcesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/directory_pull_sources/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/directory_pull_sources/{id}} - Change the source name, status (active or paused), config, credentials, default role or settings. Settings are merged. Scope: {@code directory_pull:write}. */
    public DirectoryPullSourcesListResponseDataItem update(String id, DirectoryPullSourcesUpdateBody body) {
        return t.request("PATCH", "/v1/directory_pull_sources/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/directory_pull_sources/{id}} - Delete a pull source and its run log. Users it created are kept. Scope: {@code directory_pull:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/directory_pull_sources/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code PUT /v1/directory_pull_sources/{id}/csv} - Upload the CSV a csv source without a URL reads on its next run (up to 5 MiB). Scope: {@code directory_pull:write}. */
    public JsonNode setCsv(String id, DirectoryPullSourcesSetCsvBody body) {
        return t.request("PUT", "/v1/directory_pull_sources/" + enc(id) + "/csv", null, body, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** The {@code syncRuns} sub-resource. */
    public DirectoryPullSourcesSyncRuns syncRuns() {
        return new DirectoryPullSourcesSyncRuns(t);
    }
}
