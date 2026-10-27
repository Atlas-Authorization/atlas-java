package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/passkey_api/usage}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasskeyApiUsageResponse(
        String object,
        Long passkeys) {
}
