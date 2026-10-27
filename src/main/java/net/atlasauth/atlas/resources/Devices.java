package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DevicesIngestPostureBody;
import net.atlasauth.atlas.models.DevicesListResponse;
import net.atlasauth.atlas.models.DevicesListResponseDataItem;
import net.atlasauth.atlas.models.DevicesReportBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/devices} resource. */
public final class Devices {

    private final Transport t;

    public Devices(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/devices} - List registered devices, filtered by user_id or source. Scope: {@code users:read}. */
    public DevicesListResponse list() {
        return list(null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public DevicesListResponse list(String userId, String source, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("user_id", userId).set("source", source).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/devices", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/devices} - Create or refresh one device by (source, external_id), reporting managed/compliant posture. Tie it to a user with user_id or a verified user_email. Access policies read the result through the deviceTrust condition: trusted = a managed, compliant device reported within 30 days. Scope: {@code users:write}. */
    public DevicesListResponseDataItem report(DevicesReportBody body) {
        return t.request("PUT", "/v1/devices", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/devices/{id}} - Fetch a device and its latest posture. Scope: {@code users:read}. */
    public DevicesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/devices/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/devices/{id}} - Remove a device from the registry. Scope: {@code users:write}. */
    public JsonNode remove(String id) {
        return t.request("DELETE", "/v1/devices/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/device_posture} - Posture ingestion for your MDM/EDR or its relay: up to 500 device reports per call, same fields as PUT /v1/devices. Scope: {@code users:write}. */
    public DevicesListResponse ingestPosture(DevicesIngestPostureBody body) {
        return ingestPosture(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public DevicesListResponse ingestPosture(DevicesIngestPostureBody body, String idempotencyKey) {
        return t.request("POST", "/v1/device_posture", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
