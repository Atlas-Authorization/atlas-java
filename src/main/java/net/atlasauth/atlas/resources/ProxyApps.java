package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ProxyAppsCreateBody;
import net.atlasauth.atlas.models.ProxyAppsListResponse;
import net.atlasauth.atlas.models.ProxyAppsListResponseDataItem;
import net.atlasauth.atlas.models.ProxyAppsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/proxy_apps} resource. */
public final class ProxyApps {

    private final Transport t;

    public ProxyApps(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/proxy_apps} - List the identity-aware proxy apps - hostnames protected by the forward-auth endpoint, with their path rules. Scope: {@code proxy_apps:read}. */
    public ProxyAppsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public ProxyAppsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/proxy_apps", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/proxy_apps} - Create a proxy app: a hostname, optional upstream URL, ordered path/method rules requiring a role, permission or entitlement, public paths, and header/token injection. The hostname is unique across the deployment. Scope: {@code proxy_apps:write}. */
    public ProxyAppsListResponseDataItem create(ProxyAppsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ProxyAppsListResponseDataItem create(ProxyAppsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/proxy_apps", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/proxy_apps/{id}} - Read one identity-aware proxy app. Scope: {@code proxy_apps:read}. */
    public ProxyAppsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/proxy_apps/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/proxy_apps/{id}} - Update a proxy app. Omitted fields keep their value. Scope: {@code proxy_apps:write}. */
    public ProxyAppsListResponseDataItem update(String id, ProxyAppsUpdateBody body) {
        return t.request("PATCH", "/v1/proxy_apps/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/proxy_apps/{id}} - Delete a proxy app. Its hostname stops resolving at the forward-auth endpoint. Scope: {@code proxy_apps:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/proxy_apps/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
