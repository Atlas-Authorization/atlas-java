package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** {@code GET /v1/instance/hooks/before_user_create}. The signing secret is never returned. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BeforeUserCreateHook(
        String object,
        boolean enabled,
        String url,
        boolean signingSecretSet,
        /** 500-5000, default 3000. */ int timeoutMs,
        /** The last call Atlas made to the hook. */ LastCall lastCall,
        Long updatedAt) {

    /** The last call Atlas made to the hook. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LastCall(/** Epoch ms. */ long at, /** ok, timeout, error or invalid_response. */ String status, String error) {
    }
}
