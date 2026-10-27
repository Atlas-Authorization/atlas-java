package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/attack_protection/app_attestation_secret}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AttackProtectionRemoveAppAttestationSecretResponse(
        String object,
        Boolean deleted) {
}
