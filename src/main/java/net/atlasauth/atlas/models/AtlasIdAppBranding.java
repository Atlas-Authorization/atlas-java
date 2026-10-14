package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdAppBranding(
        String theme,
        String accentColor,
        String backgroundColor,
        String privacyPolicyUrl,
        String termsOfServiceUrl,
        AtlasDesign design,
        ResolvedDesign resolvedDesign,
        AtlasIdAppCopy copy) {
}
