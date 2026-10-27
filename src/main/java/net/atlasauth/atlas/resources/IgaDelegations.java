package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IgaDelegationsCreateBody;
import net.atlasauth.atlas.models.IgaDelegationsDeleteResponse;
import net.atlasauth.atlas.models.IgaDelegationsListResponse;
import net.atlasauth.atlas.models.IgaDelegationsListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/iga} resource. */
public final class IgaDelegations {

    private final Transport t;

    public IgaDelegations(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/iga/delegations} - List approval delegations. Scope: {@code iga:read}. */
    public IgaDelegationsListResponse list() {
        return list(null, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaDelegationsListResponse list(String delegatorUserId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("delegator_user_id", delegatorUserId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/delegations", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/delegations} - Let another user decide a person's approvals (and reviews) for a period of up to a year. Scope: {@code iga:write}. */
    public IgaDelegationsListResponseDataItem create(IgaDelegationsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaDelegationsListResponseDataItem create(IgaDelegationsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/delegations", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/iga/delegations/{id}} - End an approval delegation. Scope: {@code iga:write}. */
    public IgaDelegationsDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/iga/delegations/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
