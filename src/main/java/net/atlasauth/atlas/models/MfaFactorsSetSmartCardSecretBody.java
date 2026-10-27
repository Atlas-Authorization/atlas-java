package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/instance/smart_card_secret}. Required: secret. */
public final class MfaFactorsSetSmartCardSecretBody {
    /** Random shared secret, 24 characters or more. Required. */
    public String secret;

    public MfaFactorsSetSmartCardSecretBody() {
    }

    public MfaFactorsSetSmartCardSecretBody secret(String v) { this.secret = v; return this; }
}
