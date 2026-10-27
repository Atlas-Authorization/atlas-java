package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/instance/duo_secret}. Required: secret. */
public final class MfaFactorsSetDuoSecretBody {
    /** Duo client secret. Required. */
    public String secret;

    public MfaFactorsSetDuoSecretBody() {
    }

    public MfaFactorsSetDuoSecretBody secret(String v) { this.secret = v; return this; }
}
