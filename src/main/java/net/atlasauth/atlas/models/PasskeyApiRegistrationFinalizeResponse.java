package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/passkey_api/registration/finalize}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasskeyApiRegistrationFinalizeResponse(
        String object,
        String id,
        String userId,
        String credentialId,
        String name,
        String aaguid,
        Boolean backedUp,
        List<String> transports,
        Long lastUsedAt,
        Long createdAt) {
}
