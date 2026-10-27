package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.MfaFactorsSmartCardMappingsAddBody;
import net.atlasauth.atlas.models.MfaFactorsSmartCardMappingsListResponse;
import net.atlasauth.atlas.models.MfaFactorsSmartCardMappingsListResponseDataItem;
import net.atlasauth.atlas.models.MfaFactorsSmartCardMappingsRemoveResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class MfaFactorsSmartCardMappings {

    private final Transport t;

    public MfaFactorsSmartCardMappings(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/users/{id}/smartcard_mappings} - List the certificate attributes registered for a user. Scope: {@code users:read}. */
    public MfaFactorsSmartCardMappingsListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public MfaFactorsSmartCardMappingsListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/users/" + enc(id) + "/smartcard_mappings", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/smartcard_mappings} - Pre-register the certificate attribute that signs this user in with a smart card: kind fingerprint (SHA-256, 64 hex), serial (hex, plus the issuer DN), san_email, san_upn or subject_cn. A registered mapping only applies while its kind is listed in mfa.smartCard.mappingRules. 409 when the value is already mapped. Scope: {@code users:write}. */
    public MfaFactorsSmartCardMappingsListResponseDataItem add(String id, MfaFactorsSmartCardMappingsAddBody body) {
        return add(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MfaFactorsSmartCardMappingsListResponseDataItem add(String id, MfaFactorsSmartCardMappingsAddBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/smartcard_mappings", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/smartcard_mappings/{mappingId}} - Remove a registered certificate mapping. Scope: {@code users:write}. */
    public MfaFactorsSmartCardMappingsRemoveResponse remove(String id, String mappingId) {
        return t.request("DELETE", "/v1/users/" + enc(id) + "/smartcard_mappings/" + enc(mappingId), null, null, null, false, new TypeReference<>() {
        });
    }
}
