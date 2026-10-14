package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationEntitlements(
        String object,
        String organizationId,
        String plan,
        List<String> features,
        Map<String, Object> catalog,
        /** Numeric limits by key (e.g. seats). */
        Map<String, Object> limits) {
}
