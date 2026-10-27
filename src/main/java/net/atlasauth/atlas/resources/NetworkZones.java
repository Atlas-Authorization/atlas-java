package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.NetworkZonesCheckBody;
import net.atlasauth.atlas.models.NetworkZonesCheckResponse;
import net.atlasauth.atlas.models.NetworkZonesCreateBody;
import net.atlasauth.atlas.models.NetworkZonesListResponse;
import net.atlasauth.atlas.models.NetworkZonesListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/network_zones} resource. */
public final class NetworkZones {

    private final Transport t;

    public NetworkZones(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/network_zones} - List reusable network zones - named sets of CIDRs, countries, ASNs and anonymizer classes (tor, datacenter, vpn) that network ACL rules reference by zone_id. Scope: {@code network_acls:read}. */
    public NetworkZonesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public NetworkZonesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/network_zones", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/network_zones} - Create a network zone. `name` is unique per instance; `cidrs`, `countries` (ISO alpha-2), `asns` and `anonymizers` are unioned (any match = in the zone). Malformed entries are refused. Scope: {@code network_acls:write}. */
    public NetworkZonesListResponseDataItem create(NetworkZonesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public NetworkZonesListResponseDataItem create(NetworkZonesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/network_zones", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/network_zones/{id}} - Read one network zone. Scope: {@code network_acls:read}. */
    public NetworkZonesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/network_zones/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/network_zones/{id}} - Update a network zone. Sending any of cidrs/countries/asns/anonymizers replaces that list; omitted lists keep their value. Scope: {@code network_acls:write}. */
    public NetworkZonesListResponseDataItem update(String id, NetworkZonesCreateBody body) {
        return t.request("PATCH", "/v1/network_zones/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/network_zones/{id}} - Delete a network zone. The network ACL rules that reference it are deleted with it. Scope: {@code network_acls:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/network_zones/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/network_zones/{id}/check} - What-if: does an IP address fall inside this zone? Returns the resolved country / ASN / Tor / datacenter facts alongside the answer. Facts the instance has no IP-intelligence data for are null and never match. Scope: {@code network_acls:read}. */
    public NetworkZonesCheckResponse check(String id, NetworkZonesCheckBody body) {
        return check(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public NetworkZonesCheckResponse check(String id, NetworkZonesCheckBody body, String idempotencyKey) {
        return t.request("POST", "/v1/network_zones/" + enc(id) + "/check", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
