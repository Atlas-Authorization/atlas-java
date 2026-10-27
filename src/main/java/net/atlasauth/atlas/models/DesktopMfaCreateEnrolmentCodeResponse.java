package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/desktop_mfa/enrolment_codes}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesktopMfaCreateEnrolmentCodeResponse(
        String object,
        String code,
        Long expiresAt,
        String userId) {
}
