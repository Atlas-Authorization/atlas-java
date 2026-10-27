package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.VcPresentationRequestsCreateBody;
import net.atlasauth.atlas.models.VcPresentationRequestsCreateResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/vc} resource. */
public final class VcPresentationRequests {

    private final Transport t;

    public VcPresentationRequests(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/vc/presentation_requests} - Start a verifier request for a credential type and claims. Show authorization_request_uri to the wallet, then read the request for the outcome. Scope: {@code vc:write}. */
    public VcPresentationRequestsCreateResponse create(VcPresentationRequestsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public VcPresentationRequestsCreateResponse create(VcPresentationRequestsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/vc/presentation_requests", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/vc/presentation_requests/{id}} - Read a presentation request and its outcome. Scope: {@code vc:read}. */
    public VcPresentationRequestsCreateResponse get(String id) {
        return t.request("GET", "/v1/vc/presentation_requests/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
