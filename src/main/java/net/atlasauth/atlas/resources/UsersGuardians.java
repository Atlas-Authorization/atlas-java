package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UsersGuardiansApproveBody;
import net.atlasauth.atlas.models.UsersGuardiansRequestBody;
import net.atlasauth.atlas.models.UsersGuardiansRequestResponse;
import net.atlasauth.atlas.models.UsersMinorStatusResponseGuardiansItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class UsersGuardians {

    private final Transport t;

    public UsersGuardians(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/users/{id}/guardians} - Ask a parent or guardian to approve a minor's account. Name them by email or by guardian_user_id (their address is used). Returns the pending link with a one-time consent_url that expires after compliance.minors.consentTtlHours. With send_email: true Atlas mails it through the application's own email provider (422 when none is set up); otherwise deliver consent_url yourself. -&gt; 201. Scope: {@code users:write}. */
    public UsersGuardiansRequestResponse request(String id, UsersGuardiansRequestBody body) {
        return request(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersGuardiansRequestResponse request(String id, UsersGuardiansRequestBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/guardians", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/guardians/{linkId}/approve} - Record consent obtained another way (a signed form, a verified call): the link becomes active with consent_method admin and the account is consented. 409 for a revoked link. Scope: {@code users:write}. */
    public UsersMinorStatusResponseGuardiansItem approve(String id, String linkId, UsersGuardiansApproveBody body) {
        return approve(id, linkId, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersMinorStatusResponseGuardiansItem approve(String id, String linkId, UsersGuardiansApproveBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/guardians/" + enc(linkId) + "/approve", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/guardians/{linkId}} - Withdraw a guardian link. When no active guardian is left, the account needs consent again. Scope: {@code users:write}. */
    public UsersMinorStatusResponseGuardiansItem revoke(String id, String linkId) {
        return t.request("DELETE", "/v1/users/" + enc(id) + "/guardians/" + enc(linkId), null, null, null, false, new TypeReference<>() {
        });
    }
}
