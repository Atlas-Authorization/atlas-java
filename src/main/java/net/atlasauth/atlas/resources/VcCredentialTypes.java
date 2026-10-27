package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.VcCredentialTypesCreateBody;
import net.atlasauth.atlas.models.VcCredentialTypesDeleteResponse;
import net.atlasauth.atlas.models.VcCredentialTypesListResponse;
import net.atlasauth.atlas.models.VcCredentialTypesListResponseDataItem;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/vc} resource. */
public final class VcCredentialTypes {

    private final Transport t;

    public VcCredentialTypes(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/vc/credential_types} - List the credential types you issue. Scope: {@code vc:read}. */
    public VcCredentialTypesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public VcCredentialTypesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/vc/credential_types", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/vc/credential_types} - Define a credential type: its vct, and how each claim is filled from a user's attributes. Issued as SD-JWT VC. Scope: {@code vc:write}. */
    public VcCredentialTypesListResponseDataItem create(VcCredentialTypesCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public VcCredentialTypesListResponseDataItem create(VcCredentialTypesCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/vc/credential_types", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/vc/credential_types/{id}} - Read one credential type. Scope: {@code vc:read}. */
    public VcCredentialTypesListResponseDataItem get(String id) {
        return t.request("GET", "/v1/vc/credential_types/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/vc/credential_types/{id}} - Delete a credential type and the records of what was issued under it. Scope: {@code vc:write}. */
    public VcCredentialTypesDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/vc/credential_types/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
