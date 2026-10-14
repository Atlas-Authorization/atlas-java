package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.atlasauth.atlas.http.Json;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationPolicy(
        String object,
        String organizationId,
        Map<String, Object> policy) {

    /** The policy overrides as a typed {@link OrgPolicy} (unknown keys land in {@link OrgPolicy#extra()}). */
    public OrgPolicy typed() {
        if (policy == null) {
            return new OrgPolicy();
        }
        return Json.mapper().convertValue(policy, OrgPolicy.class);
    }
}
