package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code PUT /v1/organizations/{id}/entitlements}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FlagsAssignOrganizationEntitlementsResponse(
        String object,
        String subjectType,
        String subjectId,
        String plan,
        List<String> features,
        Map<String, Object> limits,
        Long expiresAt) {
}
