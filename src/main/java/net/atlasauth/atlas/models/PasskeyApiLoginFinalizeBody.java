package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/passkey_api/login/finalize}. Required: handle, challenge, credentialId, authenticatorData, clientDataJson, signature. */
public final class PasskeyApiLoginFinalizeBody {
    /** Required. */
    public String handle;
    /** Required. */
    public String challenge;
    /** Required. */
    public String credentialId;
    /** Required. */
    public String authenticatorData;
    /** Required. */
    public String clientDataJson;
    /** Required. */
    public String signature;
    /** Your own user id. Atlas stores it with the passkey and never needs a matching Atlas user. */
    public String userId;

    public PasskeyApiLoginFinalizeBody() {
    }

    public PasskeyApiLoginFinalizeBody handle(String v) { this.handle = v; return this; }
    public PasskeyApiLoginFinalizeBody challenge(String v) { this.challenge = v; return this; }
    public PasskeyApiLoginFinalizeBody credentialId(String v) { this.credentialId = v; return this; }
    public PasskeyApiLoginFinalizeBody authenticatorData(String v) { this.authenticatorData = v; return this; }
    public PasskeyApiLoginFinalizeBody clientDataJson(String v) { this.clientDataJson = v; return this; }
    public PasskeyApiLoginFinalizeBody signature(String v) { this.signature = v; return this; }
    public PasskeyApiLoginFinalizeBody userId(String v) { this.userId = v; return this; }
}
