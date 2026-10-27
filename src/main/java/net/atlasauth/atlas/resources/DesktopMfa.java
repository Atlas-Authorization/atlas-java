package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DesktopMfaCreateEnrolmentCodeBody;
import net.atlasauth.atlas.models.DesktopMfaCreateEnrolmentCodeResponse;
import net.atlasauth.atlas.models.DesktopMfaDeleteDeviceResponse;
import net.atlasauth.atlas.models.DesktopMfaGetDeviceResponse;
import net.atlasauth.atlas.models.DesktopMfaGetPolicyResponse;
import net.atlasauth.atlas.models.DesktopMfaListDevicesResponse;
import net.atlasauth.atlas.models.DesktopMfaListDevicesResponseDataItem;
import net.atlasauth.atlas.models.DesktopMfaUpdatePolicyBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/desktop_mfa} resource. */
public final class DesktopMfa {

    private final Transport t;

    public DesktopMfa(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/desktop_mfa/policy} - The desktop-login MFA policy: who must pass a second factor at OS login, the allowed factors and the offline grace window. Scope: {@code desktop_mfa:read}. */
    public DesktopMfaGetPolicyResponse getPolicy() {
        return t.request("GET", "/v1/desktop_mfa/policy", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/desktop_mfa/policy} - Change the desktop-login MFA policy. Send only the fields to change. `applies_to: "selected"` needs at least one user or organization while enabled. Scope: {@code desktop_mfa:write}. */
    public DesktopMfaGetPolicyResponse updatePolicy(DesktopMfaUpdatePolicyBody body) {
        return t.request("PUT", "/v1/desktop_mfa/policy", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/desktop_mfa/enrolment_codes} - Issue a single-use code a user types into the workstation agent to enrol it. Valid for `expires_in_minutes` (default 60, at most 7 days). Shown once. Scope: {@code desktop_mfa:write}. */
    public DesktopMfaCreateEnrolmentCodeResponse createEnrolmentCode(DesktopMfaCreateEnrolmentCodeBody body) {
        return createEnrolmentCode(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public DesktopMfaCreateEnrolmentCodeResponse createEnrolmentCode(DesktopMfaCreateEnrolmentCodeBody body, String idempotencyKey) {
        return t.request("POST", "/v1/desktop_mfa/enrolment_codes", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/desktop_mfa/devices} - List enrolled workstations, optionally for one `user_id`. Scope: {@code desktop_mfa:read}. */
    public DesktopMfaListDevicesResponse listDevices() {
        return listDevices(null, null, null);
    }

    /** As above, with every optional parameter. */
    public DesktopMfaListDevicesResponse listDevices(String userId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("user_id", userId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/desktop_mfa/devices", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/desktop_mfa/devices/{id}} - Read one workstation with the offline verifiers issued to it. Scope: {@code desktop_mfa:read}. */
    public DesktopMfaGetDeviceResponse getDevice(String id) {
        return t.request("GET", "/v1/desktop_mfa/devices/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/desktop_mfa/devices/{id}} - Delete a workstation and its device key. Scope: {@code desktop_mfa:write}. */
    public DesktopMfaDeleteDeviceResponse deleteDevice(String id) {
        return t.request("DELETE", "/v1/desktop_mfa/devices/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/desktop_mfa/devices/{id}/revoke} - Revoke a workstation. Its next call is refused with `device_status: revoked` and it wipes its offline cache; offline unlock stops working at the latest when the cached verifier expires. Scope: {@code desktop_mfa:write}. */
    public DesktopMfaListDevicesResponseDataItem revokeDevice(String id) {
        return revokeDevice(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public DesktopMfaListDevicesResponseDataItem revokeDevice(String id, String idempotencyKey) {
        return t.request("POST", "/v1/desktop_mfa/devices/" + enc(id) + "/revoke", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/desktop_mfa/devices/{id}/revoke_offline_cache} - Void every offline verifier issued to the workstation without revoking it. It receives a fresh one on its next sync. Scope: {@code desktop_mfa:write}. */
    public DesktopMfaListDevicesResponseDataItem revokeOfflineCache(String id) {
        return revokeOfflineCache(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public DesktopMfaListDevicesResponseDataItem revokeOfflineCache(String id, String idempotencyKey) {
        return t.request("POST", "/v1/desktop_mfa/devices/" + enc(id) + "/revoke_offline_cache", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
