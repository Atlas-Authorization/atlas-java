package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/identity_verification/settings}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityVerificationSettingsResponse(
        String object,
        Boolean enabled,
        String connectorId,
        Boolean asChallenge,
        String webhookUrl) {
}
