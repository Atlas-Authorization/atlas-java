package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/passkey_api/registration/init}. Required: userId. */
public final class PasskeyApiRegistrationInitBody {
    /** Your own user id. Atlas stores it with the passkey and never needs a matching Atlas user. Required. */
    public String userId;
    /** Shown by the authenticator. Defaults to user_id. */
    public String username;
    public String displayName;

    public PasskeyApiRegistrationInitBody() {
    }

    public PasskeyApiRegistrationInitBody userId(String v) { this.userId = v; return this; }
    public PasskeyApiRegistrationInitBody username(String v) { this.username = v; return this; }
    public PasskeyApiRegistrationInitBody displayName(String v) { this.displayName = v; return this; }
}
