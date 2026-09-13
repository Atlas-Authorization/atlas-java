package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationHierarchy(
        String object,
        String organizationId,
        List<OrgRef> ancestors,
        List<OrgRef> children) {
}
