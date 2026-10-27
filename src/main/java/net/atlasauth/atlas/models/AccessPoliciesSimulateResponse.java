package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/access_policies/simulate}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessPoliciesSimulateResponse(
        String object,
        String decision,
        List<String> matched,
        String blockedBy,
        AccessPoliciesSimulateResponseRequirements requirements,
        List<String> unmet) {
}
