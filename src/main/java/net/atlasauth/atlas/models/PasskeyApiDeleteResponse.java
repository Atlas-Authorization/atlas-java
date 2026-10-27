package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/passkey_api/users/{userId}/passkeys/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasskeyApiDeleteResponse(
        String object,
        String id,
        Boolean deleted) {
}
