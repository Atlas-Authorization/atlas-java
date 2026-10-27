package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UsersNotesCreateBody;
import net.atlasauth.atlas.models.UsersNotesDeleteResponse;
import net.atlasauth.atlas.models.UsersNotesListResponse;
import net.atlasauth.atlas.models.UsersNotesListResponseDataItem;
import net.atlasauth.atlas.models.UsersNotesUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class UsersNotes {

    private final Transport t;

    public UsersNotes(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/users/{id}/notes} - The notes on a user: pinned notes first, then newest first. Scope: {@code users:read}. */
    public UsersNotesListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public UsersNotesListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/users/" + enc(id) + "/notes", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/notes} - Add a note to a user (up to 5000 characters; at most 500 notes per user). The audit log records that a note was added, never its text. -&gt; 201. Scope: {@code users:write}. */
    public UsersNotesListResponseDataItem create(String id, UsersNotesCreateBody body) {
        return create(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersNotesListResponseDataItem create(String id, UsersNotesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/notes", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/users/{id}/notes/{noteId}} - Edit a note: its text, its pin, or both. Scope: {@code users:write}. */
    public UsersNotesListResponseDataItem update(String id, String noteId, UsersNotesUpdateBody body) {
        return t.request("PATCH", "/v1/users/" + enc(id) + "/notes/" + enc(noteId), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/notes/{noteId}} - Delete a note from a user. The audit log keeps that it was deleted. Scope: {@code users:write}. */
    public UsersNotesDeleteResponse delete(String id, String noteId) {
        return t.request("DELETE", "/v1/users/" + enc(id) + "/notes/" + enc(noteId), null, null, null, false, new TypeReference<>() {
        });
    }
}
