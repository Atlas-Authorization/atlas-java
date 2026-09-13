package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/user_imports}. */
public final class ImportExportImportUsersBody {
    public List<ImportExportUserRow> users;
    /** Update an existing user matched by verified email instead of skipping it. */
    public Boolean upsert;

    public ImportExportImportUsersBody() {
    }

    public ImportExportImportUsersBody(List<ImportExportUserRow> users) {
        this.users = users;
    }

    public ImportExportImportUsersBody users(List<ImportExportUserRow> v) { this.users = v; return this; }
    public ImportExportImportUsersBody upsert(Boolean v) { this.upsert = v; return this; }
}
