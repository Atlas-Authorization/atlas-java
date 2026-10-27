package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UsersSearchExportBody;
import net.atlasauth.atlas.models.UsersSearchQueryBody;
import net.atlasauth.atlas.models.UsersSearchQueryResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class UsersSearch {

    private final Transport t;

    public UsersSearch(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/users/search} - Search users with a boolean filter tree (and/or/not) over profile columns, traits and metadata keys, organization and org role, MFA enrolled/strategy, sign-in strategy, last sign-in country or IP, and date ranges. Sort with order_by; page with starting_after (creation/update order) or offset (last_sign_in_at order). include_total_count adds total_count. Containment tests on public metadata and traits are answered by an index. Same user objects as GET /v1/users. Scope: {@code users:read}. */
    public UsersSearchQueryResponse query(UsersSearchQueryBody body) {
        return query(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersSearchQueryResponse query(UsersSearchQueryBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/search", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/search/export} - Export every user a search matches (up to 10,000; x-export-truncated says when more matched) as CSV (default) or NDJSON. fields picks columns: id, primary_email, username, first_name, last_name, external_id, created_at, last_sign_in_at, banned, mfa_enrolled, plus traits.&lt;key&gt; / public_metadata.&lt;path&gt;. CSV cells that start with = + - &#64; are prefixed with an apostrophe so a spreadsheet never runs them as formulas. Scope: {@code users:read}. */
    public JsonNode export(UsersSearchExportBody body) {
        return export(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode export(UsersSearchExportBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/search/export", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** The {@code views} sub-resource. */
    public UsersSearchViews views() {
        return new UsersSearchViews(t);
    }
}
