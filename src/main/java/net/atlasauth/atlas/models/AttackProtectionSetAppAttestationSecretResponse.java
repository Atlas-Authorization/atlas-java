package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code PUT /v1/attack_protection/app_attestation_secret}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AttackProtectionSetAppAttestationSecretResponse(
        String object,
        Boolean set) {
}
