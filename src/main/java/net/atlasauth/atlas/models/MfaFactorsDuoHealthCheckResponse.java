package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/instance/duo_health_check}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MfaFactorsDuoHealthCheckResponse(
        String object,
        Boolean ok,
        String message) {
}
