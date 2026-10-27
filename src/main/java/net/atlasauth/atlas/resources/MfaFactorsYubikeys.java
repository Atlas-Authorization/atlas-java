package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.MfaFactorsYubikeysListResponse;
import net.atlasauth.atlas.models.MfaFactorsYubikeysListResponseDataItem;
import net.atlasauth.atlas.models.MfaFactorsYubikeysRegisterBody;
import net.atlasauth.atlas.models.MfaFactorsYubikeysRemoveResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/yubikeys} resource. */
public final class MfaFactorsYubikeys {

    private final Transport t;

    public MfaFactorsYubikeys(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/yubikeys} - List the registered self-held YubiKeys (public id, owner, replay counter). The AES keys are never returned. Scope: {@code users:read}. */
    public MfaFactorsYubikeysListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public MfaFactorsYubikeysListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/yubikeys", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/yubikeys} - Register a YubiKey's AES key for self-held Yubico OTP (mfa.yubikey.mode "self_held"). The key is stored encrypted and never returned. Give user_id to assign it; otherwise the first user to enrol it with a valid OTP claims it. 409 when the public id is already registered. Scope: {@code users:write}. */
    public MfaFactorsYubikeysListResponseDataItem register(MfaFactorsYubikeysRegisterBody body) {
        return register(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MfaFactorsYubikeysListResponseDataItem register(MfaFactorsYubikeysRegisterBody body, String idempotencyKey) {
        return t.request("POST", "/v1/yubikeys", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/yubikeys/{id}} - Remove a registered YubiKey. A factor already enrolled with it stops verifying. Scope: {@code users:write}. */
    public MfaFactorsYubikeysRemoveResponse remove(String id) {
        return t.request("DELETE", "/v1/yubikeys/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
