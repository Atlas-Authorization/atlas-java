package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DesignSettings(
        String object,
        String organizationId,
        AtlasDesign design,
        ResolvedDesign resolved,
        List<DesignWarning> warnings,
        boolean legacy,
        AtlasDesign legacyDesign) {
}
