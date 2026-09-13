package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.ImportExportImportUsersBody;
import net.atlasauth.atlas.models.ImportExportJob;
import net.atlasauth.atlas.models.ImportExportJobError;
import net.atlasauth.atlas.models.ListPage;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The bulk user import/export ({@code jobs}) resource. */
public final class ImportExport {

    private final Transport t;

    public ImportExport(Transport t) {
        this.t = t;
    }

    public ImportExportJob importUsers(ImportExportImportUsersBody body) {
        return importUsers(body, null);
    }

    /** Import users through the same per-row path sign-up uses. Deduped by verified email. */
    public ImportExportJob importUsers(ImportExportImportUsersBody body, String idempotencyKey) {
        return t.post("/v1/user_imports", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public ImportExportJob exportUsers() {
        return exportUsers(null);
    }

    /** Export every non-deleted user in the instance, serialised without secrets. */
    public ImportExportJob exportUsers(String idempotencyKey) {
        return t.post("/v1/user_exports", null, idempotencyKey, new TypeReference<>() {
        });
    }

    public CursorPage<ImportExportJob> listJobs() {
        return listJobs(null, null);
    }

    /** Poll the instance's import/export jobs, newest first. */
    public CursorPage<ImportExportJob> listJobs(Integer limit, String startingAfter) {
        Map<String, Object> q = Params.of()
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .map();
        return t.get("/v1/jobs", q, new TypeReference<>() {
        });
    }

    public ImportExportJob getJob(String id) {
        return t.get("/v1/jobs/" + enc(id), new TypeReference<>() {
        });
    }

    /** The per-row failures recorded against a job. */
    public ListPage<ImportExportJobError> getJobErrors(String id) {
        return t.get("/v1/jobs/" + enc(id) + "/errors", new TypeReference<>() {
        });
    }
}
