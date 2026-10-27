package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/passkey_api/login/finalize}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasskeyApiLoginFinalizeResponse(
        String object,
        Boolean verified,
        String userId,
        String credentialId,
        String passkeyId,
        Boolean userVerified,
        Boolean clonedWarning) {
}
