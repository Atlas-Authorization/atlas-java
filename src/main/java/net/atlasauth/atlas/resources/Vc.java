package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.VcCreateOfferBody;
import net.atlasauth.atlas.models.VcCreateOfferResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/vc} resource. */
public final class Vc {

    private final Transport t;

    public Vc(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/vc/offers} - Create a wallet offer for a user (pre-authorized code flow). The response carries the code once, as an openid-credential-offer:// link. Scope: {@code vc:write}. */
    public VcCreateOfferResponse createOffer(VcCreateOfferBody body) {
        return createOffer(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public VcCreateOfferResponse createOffer(VcCreateOfferBody body, String idempotencyKey) {
        return t.request("POST", "/v1/vc/offers", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** The {@code credentialTypes} sub-resource. */
    public VcCredentialTypes credentialTypes() {
        return new VcCredentialTypes(t);
    }

    /** The {@code credentials} sub-resource. */
    public VcCredentials credentials() {
        return new VcCredentials(t);
    }

    /** The {@code presentationRequests} sub-resource. */
    public VcPresentationRequests presentationRequests() {
        return new VcPresentationRequests(t);
    }
}
