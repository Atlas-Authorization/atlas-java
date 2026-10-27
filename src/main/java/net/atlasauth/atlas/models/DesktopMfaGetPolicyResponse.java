package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/desktop_mfa/policy}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesktopMfaGetPolicyResponse(
        String object,
        Boolean enabled,
        String appliesTo,
        List<String> userIds,
        List<String> orgIds,
        List<String> allowedFactors,
        Boolean offlineEnabled,
        Long offlineGraceHours,
        Boolean offlinePinRequired) {
}
