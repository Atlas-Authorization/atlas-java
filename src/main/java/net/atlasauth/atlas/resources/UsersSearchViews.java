package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UsersSearchViewsCreateBody;
import net.atlasauth.atlas.models.UsersSearchViewsDeleteResponse;
import net.atlasauth.atlas.models.UsersSearchViewsListResponse;
import net.atlasauth.atlas.models.UsersSearchViewsListResponseDataItem;
import net.atlasauth.atlas.models.UsersSearchViewsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class UsersSearchViews {

    private final Transport t;

    public UsersSearchViews(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/users/search/views} - The saved user-search views of the instance, by name. Scope: {@code users:read}. */
    public UsersSearchViewsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public UsersSearchViewsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/users/search/views", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/search/views} - Save a named filter (and sort) to re-run. A filter that could never run is refused; a duplicate name is a 400. -&gt; 201. Scope: {@code users:write}. */
    public UsersSearchViewsListResponseDataItem create(UsersSearchViewsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersSearchViewsListResponseDataItem create(UsersSearchViewsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/search/views", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/users/search/views/{id}} - Rename a saved view or change its filter or sort. Scope: {@code users:write}. */
    public UsersSearchViewsListResponseDataItem update(String id, UsersSearchViewsUpdateBody body) {
        return t.request("PATCH", "/v1/users/search/views/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/search/views/{id}} - Delete a saved user-search view. Scope: {@code users:write}. */
    public UsersSearchViewsDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/users/search/views/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
