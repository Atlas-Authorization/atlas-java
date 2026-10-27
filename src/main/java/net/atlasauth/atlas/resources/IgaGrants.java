package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IgaGrantsCreateBody;
import net.atlasauth.atlas.models.IgaGrantsListResponse;
import net.atlasauth.atlas.models.IgaGrantsListResponseDataItem;
import net.atlasauth.atlas.models.IgaGrantsRevokeBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/iga} resource. */
public final class IgaGrants {

    private final Transport t;

    public IgaGrants(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/iga/grants} - List grants (what each user was given through governance), newest first. Scope: {@code iga:read}. */
    public IgaGrantsListResponse list() {
        return list(null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaGrantsListResponse list(String userId, String entitlement, String status, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("user_id", userId).set("entitlement", entitlement).set("status", status).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/grants", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/grants} - Give a user an entitlement now, without a request. `expires_at` makes it time-bound. A blocking separation-of-duties rule refuses it; a require-approval rule refuses unless `override_reason` is given, which records an accepted violation. Scope: {@code iga:write}. */
    public IgaGrantsListResponseDataItem create(IgaGrantsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaGrantsListResponseDataItem create(IgaGrantsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/grants", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/grants/{id}/revoke} - Take the access away and close the grant, including access that existed before governance adopted it (an expiry, by contrast, leaves adopted access in place). Scope: {@code iga:write}. */
    public IgaGrantsListResponseDataItem revoke(String id, IgaGrantsRevokeBody body) {
        return revoke(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaGrantsListResponseDataItem revoke(String id, IgaGrantsRevokeBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/grants/" + enc(id) + "/revoke", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
