package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.VcCredentialsListResponse;
import net.atlasauth.atlas.models.VcCredentialsListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/vc} resource. */
public final class VcCredentials {

    private final Transport t;

    public VcCredentials(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/vc/credentials} - List issued credentials (most recent 200), optionally for one user. Scope: {@code vc:read}. */
    public VcCredentialsListResponse list() {
        return list(null, null, null);
    }

    /** As above, with every optional parameter. */
    public VcCredentialsListResponse list(String userId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("user_id", userId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/vc/credentials", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/vc/credentials/{id}/revoke} - Revoke an issued credential: its status-list bit is set and later presentations are refused. Scope: {@code vc:write}. */
    public VcCredentialsListResponseDataItem revoke(String id, Map<String, Object> body) {
        return revoke(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public VcCredentialsListResponseDataItem revoke(String id, Map<String, Object> body, String idempotencyKey) {
        return t.request("POST", "/v1/vc/credentials/" + enc(id) + "/revoke", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
