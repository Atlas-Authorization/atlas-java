package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationEntitlements(
        String object,
        String organizationId,
        String plan,
        Map<String, Object> features,
        Map<String, Object> catalog) {
}
