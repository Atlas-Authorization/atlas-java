package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/attack_protection/app_attestation_secret}. */
public final class AttackProtectionSetAppAttestationSecretBody {
    public String secret;
    public String kind;

    public AttackProtectionSetAppAttestationSecretBody() {
    }

    public AttackProtectionSetAppAttestationSecretBody secret(String v) { this.secret = v; return this; }
    public AttackProtectionSetAppAttestationSecretBody kind(String v) { this.kind = v; return this; }
}
