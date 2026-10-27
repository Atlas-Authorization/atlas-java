package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/instance/yubico_secret}. Required: secret. */
public final class MfaFactorsSetYubicoSecretBody {
    /** Base64 secret key. Required. */
    public String secret;

    public MfaFactorsSetYubicoSecretBody() {
    }

    public MfaFactorsSetYubicoSecretBody secret(String v) { this.secret = v; return this; }
}
