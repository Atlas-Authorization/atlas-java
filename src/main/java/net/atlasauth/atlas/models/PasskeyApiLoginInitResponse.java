package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/passkey_api/login/init}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasskeyApiLoginInitResponse(
        String object,
        String handle,
        Map<String, Object> options) {
}
