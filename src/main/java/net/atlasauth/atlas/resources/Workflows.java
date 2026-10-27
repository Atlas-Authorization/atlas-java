package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.WorkflowsCreateBody;
import net.atlasauth.atlas.models.WorkflowsDeleteResponse;
import net.atlasauth.atlas.models.WorkflowsDryRunBody;
import net.atlasauth.atlas.models.WorkflowsDryRunResponse;
import net.atlasauth.atlas.models.WorkflowsListResponse;
import net.atlasauth.atlas.models.WorkflowsListResponseDataItem;
import net.atlasauth.atlas.models.WorkflowsRunsResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/workflows} resource. */
public final class Workflows {

    private final Transport t;

    public Workflows(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/workflows} - List lifecycle workflows: a trigger (cron, user created, inactive for N days, membership age, trial end, webhook event) plus ordered steps, run by the worker. Scope: {@code actions:read}. */
    public WorkflowsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public WorkflowsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/workflows", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/workflows} - Create a lifecycle workflow. Steps are notify, disable, delete, invite, add_to_org, remove_from_org, add_to_team, remove_from_team, add_to_group, remove_from_group (an instance group by id or slug) and call_action (an Action with the `workflow_step` trigger, which may be an `http` Action). Each subject runs once per workflow; the first failing step stops the run. Scope: {@code actions:write}. */
    public WorkflowsListResponseDataItem create(WorkflowsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public WorkflowsListResponseDataItem create(WorkflowsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/workflows", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/workflows/{id}} - Read one lifecycle workflow. Scope: {@code actions:read}. */
    public WorkflowsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/workflows/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/workflows/{id}} - Update a workflow's name, enabled flag, trigger or steps. Changing the trigger resets its cron slot and event cursor. Scope: {@code actions:write}. */
    public WorkflowsListResponseDataItem update(String id, WorkflowsCreateBody body) {
        return t.request("PATCH", "/v1/workflows/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/workflows/{id}} - Delete a workflow and its run history. Scope: {@code actions:write}. */
    public WorkflowsDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/workflows/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/workflows/{id}/dry_run} - Check what a workflow would do for a user (`user_id`; none for cron) without changing anything. Every step is validated against live data and reported as `would_run` or `failed`. Scope: {@code actions:write}. */
    public WorkflowsDryRunResponse dryRun(String id, WorkflowsDryRunBody body) {
        return dryRun(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public WorkflowsDryRunResponse dryRun(String id, WorkflowsDryRunBody body, String idempotencyKey) {
        return t.request("POST", "/v1/workflows/" + enc(id) + "/dry_run", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/workflows/{id}/run} - Run a workflow now for one user (`user_id`; none for cron), outside its trigger. Returns the finished run. Scope: {@code actions:write}. */
    public WorkflowsDryRunResponse run(String id, WorkflowsDryRunBody body) {
        return run(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public WorkflowsDryRunResponse run(String id, WorkflowsDryRunBody body, String idempotencyKey) {
        return t.request("POST", "/v1/workflows/" + enc(id) + "/run", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/workflows/{id}/runs} - Run history, newest first, dry runs included (`dry_run`). Scope: {@code actions:read}. */
    public WorkflowsRunsResponse runs(String id) {
        return runs(id, null, null);
    }

    /** As above, with every optional parameter. */
    public WorkflowsRunsResponse runs(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/workflows/" + enc(id) + "/runs", q, null, null, false, new TypeReference<>() {
        });
    }
}
