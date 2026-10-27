package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/vc/offers}. Required: credentialTypeId, userId. */
public final class VcCreateOfferBody {
    /** Required. */
    public String credentialTypeId;
    /** Required. */
    public String userId;
    /** 4 to 8 digits the wallet must send, delivered to the user separately. */
    public String txCode;
    /** Default 600. */
    public Long expiresInSeconds;

    public VcCreateOfferBody() {
    }

    public VcCreateOfferBody credentialTypeId(String v) { this.credentialTypeId = v; return this; }
    public VcCreateOfferBody userId(String v) { this.userId = v; return this; }
    public VcCreateOfferBody txCode(String v) { this.txCode = v; return this; }
    public VcCreateOfferBody expiresInSeconds(Long v) { this.expiresInSeconds = v; return this; }
}
