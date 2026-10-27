package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.MigrationsImportsApplyBody;
import net.atlasauth.atlas.models.MigrationsImportsCreateBody;
import net.atlasauth.atlas.models.MigrationsImportsCreateResponse;
import net.atlasauth.atlas.models.MigrationsImportsDeleteResponse;
import net.atlasauth.atlas.models.MigrationsImportsGetPublicKeyResponse;
import net.atlasauth.atlas.models.MigrationsImportsListResponse;
import net.atlasauth.atlas.models.MigrationsImportsPutPartBody;
import net.atlasauth.atlas.models.MigrationsImportsPutPartResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/migrations} resource. */
public final class MigrationsImports {

    private final Transport t;

    public MigrationsImports(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/migrations/{source}/imports/public_key} - The instance's import public key (created on first read). Seal the export to it; only this instance can open the bundle. One key serves every source. A key with jobs:write may read it too. Scope: {@code jobs:read}. */
    public MigrationsImportsGetPublicKeyResponse getPublicKey(String source) {
        return t.request("GET", "/v1/migrations/" + enc(source) + "/imports/public_key", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/migrations/{source}/imports/public_key/rotate} - Replace the import key. A bundle sealed to the old key no longer finalizes; export again with the new key. Scope: {@code jobs:write}. */
    public MigrationsImportsGetPublicKeyResponse rotatePublicKey(String source) {
        return rotatePublicKey(source, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MigrationsImportsGetPublicKeyResponse rotatePublicKey(String source, String idempotencyKey) {
        return t.request("POST", "/v1/migrations/" + enc(source) + "/imports/public_key/rotate", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/migrations/{source}/imports} - Start an import from `source` (status `uploading`). Then upload the sealed bundle with PUT .../parts/&#123;part&#125; and call .../finalize. Nothing is written to users until apply. Scope: {@code jobs:write}. */
    public MigrationsImportsCreateResponse create(String source, MigrationsImportsCreateBody body) {
        return create(source, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MigrationsImportsCreateResponse create(String source, MigrationsImportsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/migrations/" + enc(source) + "/imports", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/migrations/{source}/imports} - List the imports from `source`, newest first (cursor pagination). Scope: {@code jobs:read}. */
    public MigrationsImportsListResponse list(String source) {
        return list(source, null, null);
    }

    /** As above, with every optional parameter. */
    public MigrationsImportsListResponse list(String source, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/migrations/" + enc(source) + "/imports", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/migrations/{source}/imports/{id}} - Fetch one import: status, the dry-run report, live progress and the apply result. Scope: {@code jobs:read}. */
    public MigrationsImportsCreateResponse get(String source, String id) {
        return t.request("GET", "/v1/migrations/" + enc(source) + "/imports/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/migrations/{source}/imports/{id}} - Delete an import and its stored bundle. Data it already imported stays. Refused (409) while analyzing or applying. Scope: {@code jobs:write}. */
    public MigrationsImportsDeleteResponse delete(String source, String id) {
        return t.request("DELETE", "/v1/migrations/" + enc(source) + "/imports/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/migrations/{source}/imports/{id}/parts/{part}} - Upload part `part` (1, 2, ...) of the sealed bundle: whole lines, at most 10 MiB, as `&#123; "data": "..." &#125;` or a text/plain body. Re-sending a part replaces it. Only while `uploading`. Scope: {@code jobs:write}. */
    public MigrationsImportsPutPartResponse putPart(String source, String id, String part, MigrationsImportsPutPartBody body) {
        return t.request("PUT", "/v1/migrations/" + enc(source) + "/imports/" + enc(id) + "/parts/" + enc(part), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/migrations/{source}/imports/{id}/finalize} - Close the upload: every part 1..n present, the bundle sealed to the active import key and in a format `source` reads (else 400 or status `invalid`). The dry run follows: inline up to 4 MiB (200, `ready`), else in the background (202, `analyzing`; poll GET). Scope: {@code jobs:write}. */
    public MigrationsImportsCreateResponse finalizeValue(String source, String id) {
        return finalizeValue(source, id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MigrationsImportsCreateResponse finalizeValue(String source, String id, String idempotencyKey) {
        return t.request("POST", "/v1/migrations/" + enc(source) + "/imports/" + enc(id) + "/finalize", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/migrations/{source}/imports/{id}/dry_run} - Run the dry run again, optionally with new options (they replace the stored ones). Reads only. Scope: {@code jobs:write}. */
    public MigrationsImportsCreateResponse dryRun(String source, String id, MigrationsImportsCreateBody body) {
        return dryRun(source, id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MigrationsImportsCreateResponse dryRun(String source, String id, MigrationsImportsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/migrations/" + enc(source) + "/imports/" + enc(id) + "/dry_run", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/migrations/{source}/imports/{id}/apply} - Import the bundle. Every record is an idempotent upsert keyed by its source id, so applying a failed import again resumes where it stopped. Up to 1,000 remaining records run inline (200), more are queued (202). With conflicts in the dry run, send confirm_conflicts: true. Scope: {@code jobs:write}. */
    public MigrationsImportsCreateResponse apply(String source, String id, MigrationsImportsApplyBody body) {
        return apply(source, id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MigrationsImportsCreateResponse apply(String source, String id, MigrationsImportsApplyBody body, String idempotencyKey) {
        return t.request("POST", "/v1/migrations/" + enc(source) + "/imports/" + enc(id) + "/apply", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
