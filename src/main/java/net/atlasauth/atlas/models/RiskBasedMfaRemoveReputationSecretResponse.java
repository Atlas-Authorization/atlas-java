package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/risk_based_mfa/reputation_secret}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiskBasedMfaRemoveReputationSecretResponse(
        String object,
        String provider,
        Boolean deleted) {
}
