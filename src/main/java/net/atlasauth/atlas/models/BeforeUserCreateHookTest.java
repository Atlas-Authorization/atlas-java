package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** {@code POST /v1/instance/hooks/before_user_create/test}: a sample draft sent to the hook; nothing is stored. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BeforeUserCreateHookTest(
        boolean ok,
        int latencyMs,
        Answer answer,
        /** code: timeout, error, invalid_response or not_configured. */ ReferralConnectionTest.TestError error) {

    /** What the hook answered. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Answer(String externalId, Map<String, Object> publicMetadata, boolean privateMetadataSet, Map<String, Object> unsafeMetadata) {
    }
}
