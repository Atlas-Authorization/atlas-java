package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/instance/duo_secret}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MfaFactorsRemoveDuoSecretResponse(
        String object,
        Boolean deleted) {
}
