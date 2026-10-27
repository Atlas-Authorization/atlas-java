package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/passkey_api/login/init}. */
public final class PasskeyApiLoginInitBody {
    /** Your own user id. Atlas stores it with the passkey and never needs a matching Atlas user. */
    public String userId;

    public PasskeyApiLoginInitBody() {
    }

    public PasskeyApiLoginInitBody userId(String v) { this.userId = v; return this; }
}
