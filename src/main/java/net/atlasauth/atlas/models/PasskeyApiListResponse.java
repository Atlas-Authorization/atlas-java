package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/passkey_api/users/{userId}/passkeys}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasskeyApiListResponse(
        String object,
        List<PasskeyApiRegistrationFinalizeResponse> data,
        Boolean hasMore,
        String nextCursor) {
}
