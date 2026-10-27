package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code PUT /v1/instance/yubico_secret}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MfaFactorsSetYubicoSecretResponse(
        String object,
        Boolean set) {
}
