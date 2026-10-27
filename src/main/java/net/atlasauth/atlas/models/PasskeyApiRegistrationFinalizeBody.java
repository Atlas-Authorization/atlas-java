package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/passkey_api/registration/finalize}. Required: userId, challenge, attestationObject, clientDataJson. */
public final class PasskeyApiRegistrationFinalizeBody {
    /** Your own user id. Atlas stores it with the passkey and never needs a matching Atlas user. Required. */
    public String userId;
    /** Required. */
    public String challenge;
    /** base64url. Required. */
    public String attestationObject;
    /** base64url. Required. */
    public String clientDataJson;
    public String name;
    public List<String> transports;

    public PasskeyApiRegistrationFinalizeBody() {
    }

    public PasskeyApiRegistrationFinalizeBody userId(String v) { this.userId = v; return this; }
    public PasskeyApiRegistrationFinalizeBody challenge(String v) { this.challenge = v; return this; }
    public PasskeyApiRegistrationFinalizeBody attestationObject(String v) { this.attestationObject = v; return this; }
    public PasskeyApiRegistrationFinalizeBody clientDataJson(String v) { this.clientDataJson = v; return this; }
    public PasskeyApiRegistrationFinalizeBody name(String v) { this.name = v; return this; }
    public PasskeyApiRegistrationFinalizeBody transports(List<String> v) { this.transports = v; return this; }
}
