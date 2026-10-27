package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IgaEntitlementsCreateBody;
import net.atlasauth.atlas.models.IgaEntitlementsDeleteResponse;
import net.atlasauth.atlas.models.IgaEntitlementsListResponse;
import net.atlasauth.atlas.models.IgaEntitlementsListResponseDataItem;
import net.atlasauth.atlas.models.IgaEntitlementsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/iga} resource. */
public final class IgaEntitlements {

    private final Transport t;

    public IgaEntitlements(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/iga/entitlements} - List the governed entitlement catalog. Scope: {@code iga:read}. */
    public IgaEntitlementsListResponse list() {
        return list(null, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaEntitlementsListResponse list(String kind, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("kind", kind).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/entitlements", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/entitlements} - Add an entitlement to the catalog: a role, organization membership, group, resource role, installed-app assignment or custom item, with an owner, a risk level and approval rules. The target must exist. 409 for a key already in use. Scope: {@code iga:write}. */
    public IgaEntitlementsListResponseDataItem create(IgaEntitlementsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaEntitlementsListResponseDataItem create(IgaEntitlementsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/entitlements", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/entitlements/{id}} - Read one entitlement by id or key. Scope: {@code iga:read}. */
    public IgaEntitlementsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/iga/entitlements/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/iga/entitlements/{id}} - Change an entitlement (its kind cannot change). A change to the approval rules applies to requests filed afterwards. Scope: {@code iga:write}. */
    public IgaEntitlementsListResponseDataItem update(String id, IgaEntitlementsUpdateBody body) {
        return t.request("PATCH", "/v1/iga/entitlements/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/iga/entitlements/{id}} - Remove an entitlement. 409 while it still has active grants. Scope: {@code iga:write}. */
    public IgaEntitlementsDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/iga/entitlements/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
